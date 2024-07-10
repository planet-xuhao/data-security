package com.rhf.common.security.crypto;

import com.rhf.common.security.crypto.exception.CryptoException;

import java.util.Collections;
import java.util.List;

/**
 * 当不存在配置时，还需要作为bean来使用。这种情况下不做加解密
 *
 * @author xuh
 * @date 2024/7/7
 */
public class EmptyDataEncryptSpec implements IDataEncryptSpec {
    @Override
    public byte[] encrypt(String operatorName, byte[] data) {
        return data;
    }

    @Override
    public byte[] decrypt(String operatorName, byte[] data) throws CryptoException {
        return data;
    }

    @Override
    public String encrypt(String operatorName, String data) throws CryptoException {
        return data;
    }

    @Override
    public String decrypt(String operatorName, String data) throws CryptoException {
        return data;
    }

    @Override
    public String decrypt(String operatorName, String data, String charset) throws CryptoException {
        return data;
    }

    @Override
    public String encrypt(String operatorName, String data, String charset) throws CryptoException {
        return data;
    }

    @Override
    public List<ObjectPlainText> encrypt(String operatorName, Object object) throws CryptoException {
        return Collections.emptyList();
    }

    @Override
    public List<ObjectPlainText> encrypt(String operatorName, Object object, String charset) throws CryptoException {
        return Collections.emptyList();
    }

    @Override
    public void decrypt(String operatorName, Object object) throws CryptoException {
        // 不做解密
    }

    @Override
    public void decrypt(String operatorName, Object object, String charset) throws CryptoException {
        // 不做解密
    }

    @Override
    public void recoveryPlainText(List<ObjectPlainText> plainTextList) {
        // 不做还原
    }
}
