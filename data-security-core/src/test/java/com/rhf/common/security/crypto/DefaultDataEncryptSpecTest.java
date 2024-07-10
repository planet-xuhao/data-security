package com.rhf.common.security.crypto;

import com.rhf.common.security.crypto.annotation.EncryptField;
import com.rhf.common.security.crypto.operator.AesOperator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author xuh
 * @date 2024/7/7
 */
public class DefaultDataEncryptSpecTest {

    private static IDataEncryptSpec dataEncryptSpec;

    private final String plainText = "张三";

    private final String expectEncryptText = "$AES_dB64RJXoqGYVBjbL6HXfcA==";

    private final String noPrefixText = "dB64RJXoqGYVBjbL6HXfcA==";

    @BeforeAll
    static void setup() {
        CryptoOperatorManager manager = new CryptoOperatorManager();
        Properties properties = new Properties();
        properties.put("key", "2poyJRokk9SFK91cY5gVuHPWge7SfdCPm9yn1tymw2k=");
        AesOperator aesOperator = new AesOperator(properties);
        manager.registerStrategy(aesOperator);
        manager.setPrimaryOperator("AES");
        dataEncryptSpec = new DefaultDataEncryptSpec(manager);
    }

    @Test
    @DisplayName("加密字符串")
    void testEncryptString() {
        // 测试空串加密
        String data = null;
        assertNull(dataEncryptSpec.encrypt(null, data));
        // 测试普通字符串
        assertEquals(dataEncryptSpec.encrypt(null, plainText), expectEncryptText);
    }

    @Test
    @DisplayName("对空串或null进行加密")
    void testEmptyStrOrNull() {
        assertNull(dataEncryptSpec.encrypt(null, (String) null));
        assertEquals("", dataEncryptSpec.encrypt(null, ""));
    }

    @Test
    @DisplayName("解密字符串")
    void decryptString() {
        String data = null;
        // 空串
        assertNull(dataEncryptSpec.decrypt(null, data));
        // 正常字符串
        assertEquals(plainText, dataEncryptSpec.decrypt(null, expectEncryptText));
        // 无前缀字符串
        assertEquals(noPrefixText, dataEncryptSpec.decrypt(null, noPrefixText));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class EncryptObject {
        @EncryptField
        private String text;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    static class DeepEncryptObject {
        private EncryptObject obj;
    }

    @Test
    @DisplayName("对象加密")
    void testEncryptObject() {
        // 普通对象
        EncryptObject normalObject = new EncryptObject(plainText);
        dataEncryptSpec.encrypt(null, normalObject);
        assertEquals(expectEncryptText, normalObject.getText());
    }

    @Test
    @DisplayName("嵌套对象加密")
    void testEncryptDeepObject() {
        // 内嵌对象
        EncryptObject nestedObj = new EncryptObject(plainText);
        DeepEncryptObject deepObject = new DeepEncryptObject(nestedObj);
        dataEncryptSpec.encrypt(null, deepObject);
        assertEquals(expectEncryptText, deepObject.getObj().getText());
    }

    @Test
    @DisplayName("集合对象加密")
    void testEncryptCollectionObject() {
        // 对象集合
        int len = 3;
        List<EncryptObject> list = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            list.add(new EncryptObject(plainText));
        }
        dataEncryptSpec.encrypt(null, list);
        for (int i = 0; i < len; i++) {
            assertEquals(expectEncryptText, list.get(i).getText());
        }
    }

    @Test
    @DisplayName("Map对象加密")
    void testEncryptMapObject() {
        int len = 3;
        Map<Integer, EncryptObject> map = new HashMap<>(len);
        for (int i = 0; i < len; i++) {
            map.put(i, new EncryptObject(plainText));
        }
        dataEncryptSpec.encrypt(null, map);
        for (Map.Entry<Integer, EncryptObject> entry : map.entrySet()) {
            assertEquals(expectEncryptText, entry.getValue().getText());
        }
    }

    @Test
    @DisplayName("异常测试")
    void testEncryptException() {
        // 空对象
        assertEquals(0, dataEncryptSpec.encrypt(null, (Object) null).size());
        // 数值对象
        int primitiveType = 1;
        assertEquals(0, dataEncryptSpec.encrypt(null, primitiveType).size());
    }

    @Test
    @DisplayName("对象解密")
    void testObjectDecrypt() {
        EncryptObject normalObject = new EncryptObject(expectEncryptText);
        dataEncryptSpec.decrypt(null, normalObject);
        assertEquals(plainText, normalObject.getText());
    }

    @Test
    @DisplayName("嵌套对象解密")
    void testDeepObjectDecrypt() {
        EncryptObject nestedObj = new EncryptObject(expectEncryptText);
        DeepEncryptObject deepObject = new DeepEncryptObject(nestedObj);
        dataEncryptSpec.decrypt(null, deepObject);
        assertEquals(plainText, deepObject.getObj().getText());
    }

    @Test
    @DisplayName("集合对象解密")
    void testCollectionObjectDecrypt() {
        int len = 3;
        List<EncryptObject> list = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            list.add(new EncryptObject(expectEncryptText));
        }
        dataEncryptSpec.decrypt(null, list);
        for (int i = 0; i < len; i++) {
            assertEquals(plainText, list.get(i).getText());
        }
    }

    @Test
    @DisplayName("Map对象解密")
    void testMapObjectDecrypt() {
        int len = 3;
        Map<Integer, EncryptObject> map = new HashMap<>(len);
        for (int i = 0; i < len; i++) {
            map.put(i, new EncryptObject(expectEncryptText));
        }
        dataEncryptSpec.decrypt(null, map);
        for (Map.Entry<Integer, EncryptObject> entry : map.entrySet()) {
            assertEquals(plainText, entry.getValue().getText());
        }
    }

    @Test
    @DisplayName("解密异常测试")
    void testDecryptException() {
        // 空对象
        try {
            dataEncryptSpec.decrypt(null, (Object) null);
        } catch (Exception e) {
            fail("解密空对象失败");
        }
        // 数值对象
        Integer primitiveType = 1;
        try {
            dataEncryptSpec.decrypt(null, primitiveType);
        } catch (Exception e) {
            fail("处理原生类型或者包装类型异常");
        }
    }

    @Test
    @DisplayName("测试恢复原文")
    void recoveryPlainText() {
        EncryptObject normalObject = new EncryptObject(plainText);
        List<ObjectPlainText> plainTextList = dataEncryptSpec.encrypt(null, normalObject);
        assertEquals(expectEncryptText, normalObject.getText());

        dataEncryptSpec.recoveryPlainText(plainTextList);
        assertEquals(plainText, normalObject.getText());
    }

    @Getter
    @Setter
    public static class EncryptToOtherFieldObj {
        @EncryptField(targetField = "targetField")
        String originField;
        String targetField;

        @EncryptField
        String normalField;
    }

    @Test
    @DisplayName("加密到其他字段")
    void testEncryptOtherField() {
        EncryptToOtherFieldObj obj = new EncryptToOtherFieldObj();
        obj.setOriginField(plainText);
        obj.setNormalField(plainText);
        dataEncryptSpec.encrypt(null, obj);
        assertEquals(expectEncryptText, obj.getTargetField());
        assertEquals(plainText, obj.getOriginField());
        assertEquals(expectEncryptText, obj.getNormalField());
    }

    @Test
    @DisplayName("从其他字段解密")
    void testDecryptFromOtherField() {
        EncryptToOtherFieldObj obj = new EncryptToOtherFieldObj();
        obj.setTargetField(expectEncryptText);
        obj.setNormalField(expectEncryptText);
        dataEncryptSpec.decrypt(null, obj);
        assertEquals(plainText, obj.getTargetField());
        assertEquals(plainText, obj.getOriginField());
        assertEquals(plainText, obj.getNormalField());
    }
}

