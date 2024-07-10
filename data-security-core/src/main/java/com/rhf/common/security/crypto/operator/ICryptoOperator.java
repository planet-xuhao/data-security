package com.rhf.common.security.crypto.operator;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 加密方式接口
 *
 * @author xuh
 * @date 2024/7/5
 */
public interface ICryptoOperator {
    byte[] encrypt(byte[] data);

    byte[] decrypt(byte[] data);

    String encryptString(String data, String charset);

    String decryptString(String data, String charset);

    String getType();

}
