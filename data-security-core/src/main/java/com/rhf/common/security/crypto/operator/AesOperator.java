package com.rhf.common.security.crypto.operator;

import com.rhf.common.security.crypto.exception.DecryptException;
import com.rhf.common.security.crypto.exception.EncryptException;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Properties;

/**
 * @author xuh
 * @date 2024/7/5
 */
public class AesOperator extends AbstractOperator {

    private static final String ALGORITHM_NAME = "AES";

    private static final String PREFIX = "$AES_";

    private final SecretKey secretKey;

    public AesOperator(Properties properties) {
        super(properties);
        // 初始化密钥
        String keyStr = (String) super.getProperties().get("key");
        // 将Base64编码的字符串解码为字节数组
        byte[] keyBytes = Base64.getDecoder().decode(keyStr);
        // 创建AES密钥对象
        secretKey = new SecretKeySpec(keyBytes, ALGORITHM_NAME);
    }

    @Override
    public byte[] encrypt(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_NAME);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new EncryptException("encrypt failed!", e);
        }
    }

    @Override
    public byte[] decrypt(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_NAME);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new EncryptException("encrypt failed!", e);
        }
    }

    @Override
    public String encryptString(String data, String charset) {
        try {
            byte[] encryptBytes = encrypt(data.getBytes(charset));
            return PREFIX + Base64.getEncoder().encodeToString(encryptBytes);
        } catch (Exception e) {
            throw new EncryptException("unSupport encoding " + charset, e);
        }
    }

    @Override
    public String decryptString(String data, String charset) {
        try {
            // 待解密字符串没有前缀则认为是原文
            if (!data.startsWith(PREFIX)) {
                return data;
            }
            data = data.replace(PREFIX, "");
            byte[] decryptBytes = decrypt(Base64.getDecoder().decode(data));
            return new String(decryptBytes, charset);
        } catch (Exception e) {
            throw new DecryptException("unSupport encoding " + charset, e);
        }
    }

    @Override
    public String getType() {
        return ALGORITHM_NAME;
    }
}
