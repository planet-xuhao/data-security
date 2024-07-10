package com.rhf.common.security.crypto;

import com.rhf.common.security.crypto.annotation.EncryptField;
import com.rhf.common.security.crypto.exception.CryptoException;
import com.rhf.common.security.crypto.exception.EncryptException;
import com.rhf.common.security.crypto.operator.ICryptoOperator;
import com.rhf.common.security.support.FieldAnnotationScanner;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author xuh
 * @date 2024/7/5
 */
public class DefaultDataEncryptSpec implements IDataEncryptSpec {

    private static final String DEFAULT_CHARSET = "utf-8";

    private final FieldAnnotationScanner<EncryptField> fieldScanner =
            new FieldAnnotationScanner<>(EncryptField.class, Collections.singletonList(String.class));

    private final CryptoOperatorManager cryptoOperatorManager;

    public DefaultDataEncryptSpec(CryptoOperatorManager cryptoOperatorManager) {
        this.cryptoOperatorManager = cryptoOperatorManager;
    }

    private ICryptoOperator getCryptoOperator(String operatorName) {
        return cryptoOperatorManager.getOperator(operatorName);
    }

    @Override
    public byte[] encrypt(String operatorName, byte[] data) {
        return getCryptoOperator(operatorName).encrypt(data);
    }

    @Override
    public byte[] decrypt(String operatorName, byte[] data) throws CryptoException {
        return getCryptoOperator(operatorName).decrypt(data);
    }

    @Override
    public String encrypt(String operatorName, String data) throws CryptoException {
        return encrypt(operatorName, data, DEFAULT_CHARSET);
    }

    @Override
    public String decrypt(String operatorName, String data) throws CryptoException {
        return decrypt(operatorName, data, DEFAULT_CHARSET);
    }

    @Override
    public String decrypt(String operatorName, String data, String charset) throws CryptoException {
        // 不对空串进行解密
        if (data == null || data.isEmpty()) {
            return data;
        }
        return getCryptoOperator(operatorName).decryptString(data, charset);
    }

    @Override
    public String encrypt(String operatorName, String data, String charset) throws CryptoException {
        // 不对空串进行加密
        if (Objects.isNull(data) || data.isEmpty()) {
            return data;
        }
        return getCryptoOperator(operatorName).encryptString(data, charset);
    }

    @Override
    public List<ObjectPlainText> encrypt(String operatorName, Object object) throws CryptoException {
        return encrypt(operatorName, object, DEFAULT_CHARSET);
    }

    @Override
    public List<ObjectPlainText> encrypt(String operatorName, Object object, String charset) throws CryptoException {
        try {
            LinkedList<ObjectPlainText> plainTextList = new LinkedList<>();
            fieldScanner.scanAndHandle(object, (instance, field, annotation) -> {
                String value = (String) field.get(instance);
                if (value == null) {
                    return;
                }
                plainTextList.add(new ObjectPlainText(value, instance, field));
                // 进行加密
                String result = encrypt(operatorName, value, charset);
                // 检查注解上是否存在指定字段，需要将其加密到指定字段上
                String targetFieldName = annotation.targetField();
                if (hasTargetFieldConfig(annotation)) {
                    // 获取目标字段
                    try {
                        Field targetField = instance.getClass().getDeclaredField(targetFieldName);
                        targetField.setAccessible(true);
                        targetField.set(instance, result);
                    } catch (NoSuchFieldException e) {
                        throw new EncryptException("no found target field " + targetFieldName +
                                ", please check EncryptField annotation value", e);
                    }
                } else {
                    // 直接加密到原字段
                    field.set(instance, result);
                }
            });
            return plainTextList;
        } catch (Exception e) {
            throw new EncryptException("access field error!", e);
        }
    }

    @Override
    public void decrypt(String operatorName, Object object) throws CryptoException {
        decrypt(operatorName, object, DEFAULT_CHARSET);
    }

    @Override
    public void decrypt(String operatorName, Object object, String charset) throws CryptoException {
        try {
            fieldScanner.scanAndHandle(object, (instance, field, annotation) -> {
                if (hasTargetFieldConfig(annotation)) {
                    // 从其他字段上解密
                    decryptFromOtherField(operatorName, instance, field, annotation, charset);
                } else {
                    // 允许字段访问
                    String value = (String) field.get(instance);
                    if (value == null || value.isEmpty()) {
                        return;
                    }
                    String decryptResult = decrypt(operatorName, value, charset);
                    // 直接对需要解密的字段赋值
                    field.set(instance, decryptResult);
                }
            });
        } catch (IllegalAccessException e) {
            throw new EncryptException("access field error!", e);
        }
    }

    @Override
    public void recoveryPlainText(List<ObjectPlainText> plainTextList) {
        if (plainTextList == null || plainTextList.isEmpty()) {
            return;
        }
        try {
            for (ObjectPlainText plainText : plainTextList) {
                plainText.getField().set(plainText.getObject(), plainText.getPlainText());
            }
        } catch (IllegalAccessException e) {
            throw new CryptoException("through reflect recovery plain text error!", e);
        }
    }

    private void decryptFromOtherField(String operatorName, Object instance, Field field, EncryptField annotation, String charset)
            throws IllegalAccessException {
        String targetFieldName = annotation.targetField();
        Field targetField;
        try {
            targetField = instance.getClass().getDeclaredField(targetFieldName);
        } catch (NoSuchFieldException e) {
            throw new EncryptException("no found target field " + targetFieldName +
                    ", please check EncryptField annotation value", e);
        }

        targetField.setAccessible(true);
        String value = (String) targetField.get(instance);
        String decryptResult;
        if (value == null || value.isEmpty()) {
            return;
        }
        decryptResult = decrypt(operatorName, value, charset);
        // 加密的字段做还原
        targetField.set(instance, decryptResult);

        // 根据注解配置判断是否要做值覆盖
        if (annotation.isOverride()) {
            field.set(instance, decryptResult);
        }
    }

    private boolean hasTargetFieldConfig(EncryptField encryptField) {
        return encryptField.targetField() != null && !encryptField.targetField().isEmpty();
    }
}
