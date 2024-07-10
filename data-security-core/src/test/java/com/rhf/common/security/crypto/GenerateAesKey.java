package com.rhf.common.security.crypto;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author xuh
 * @date 2024/7/7
 */
public class GenerateAesKey {

    public static void main(String[] args) {
        try {
            // 生成AES密钥
            SecretKey aesKey = generateAESKey();

            // 将密钥转换为Base64编码的字符串
            String base64EncodedKey = encodeAESKey(aesKey);

            System.out.println("Generated AES Key (Base64 Encoded): " + base64EncodedKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator kenGen = KeyGenerator.getInstance("AES");
        kenGen.init(256);
        return kenGen.generateKey();
    }

    private static String encodeAESKey(SecretKey key) {
        byte[] keyBytes = key.getEncoded();
        return Base64.getEncoder().encodeToString(keyBytes);
    }
}
