package com.rhf.common.security.crypto.operator;

import com.rhf.common.security.crypto.exception.CryptoException;
import com.rhf.common.security.crypto.exception.DecryptException;
import com.rhf.common.security.crypto.exception.EncryptException;
import com.rhf.common.security.crypto.operator.AbstractOperator;

import java.nio.charset.Charset;
import java.util.Properties;

/**
 * 自行实现的加解密方案，可参考该类进行拓展
 *
 * @author xuh
 * @date 2024/7/5
 */
public class ExampleOperator extends AbstractOperator {

    private static final String ALGORITHM_NAME = "CUSTOMER";

    private static final String CONFIG_PATH = "config-path";

    public ExampleOperator(Properties properties) {
        super(properties);
        String configPath = (String) properties.get(CONFIG_PATH);
        if (configPath == null || configPath.isEmpty()) {
            throw new CryptoException("config-path is null!");
        }
        // 对密钥进行初始化
    }

    @Override
    public byte[] encrypt(byte[] data) {
        throw new EncryptException("encrypt byte error!");
    }

    @Override
    public byte[] decrypt(byte[] data) {
        throw new DecryptException("decrypt byte error!");
    }

    @Override
    public String encryptString(String data, String charset) {
        throw new EncryptException("encrypt fail!");
    }

    @Override
    public String decryptString(String data, String charset) {
        throw new EncryptException("decrypt fail!");
    }

    @Override
    public String getType() {
        return ALGORITHM_NAME;
    }
}
