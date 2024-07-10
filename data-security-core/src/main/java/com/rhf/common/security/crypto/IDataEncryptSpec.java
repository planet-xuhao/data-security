package com.rhf.common.security.crypto;

import com.rhf.common.security.crypto.exception.CryptoException;

import java.util.List;

/**
 * 数据加密接口
 * 该接口主要实现数据的加密、解密、脱敏
 *
 * @author xuh
 * @date 2024/7/5
 */
public interface IDataEncryptSpec {

    /**
     * 数据加密
     *
     * @param operatorName 加密方式
     * @param data         待加密数据
     * @return 加密后数据字节数组
     */
    byte[] encrypt(String operatorName, byte[] data);

    /**
     * 数据解密
     *
     * @param operatorName 解密方式
     * @param data         待解密数据
     * @return 解密后数据字节数组
     */
    byte[] decrypt(String operatorName, byte[] data) throws CryptoException;

    /**
     * 数据加密
     *
     * @param data 待加密数据
     * @return 加密后数据
     */
    String encrypt(String operatorName,String data) throws CryptoException;

    /**
     * 数据解密
     *
     * @param data 待解密数据
     * @return 解密后数据
     */
    String decrypt(String operatorName, String data) throws CryptoException;


    /**
     * 数据解密
     *
     * @param data 待解密数据
     * @return 解密后数据
     */
    String decrypt(String operatorName, String data, String charset) throws CryptoException;

    /**
     * 数据加密
     *
     * @param data 待加密数据
     * @return 加密后数据
     */
    String encrypt(String operatorName, String data, String charset) throws CryptoException;

    /**
     * 对像加密
     *
     * @param object 待加密的对象
     */
    List<ObjectPlainText> encrypt(String operatorName, Object object) throws CryptoException;


    /**
     * 对像加密
     *
     * @param object 待加密的对象
     */
    List<ObjectPlainText> encrypt(String operatorName, Object object, String charset) throws CryptoException;

    /**
     * 对象解密，要求字段上存在注解
     *
     * @param object 待解密的对象
     */
    void decrypt(String operatorName, Object object) throws CryptoException;

    /**
     * 对象解密，要求字段上存在注解
     *
     * @param object 待解密的对象
     */
    void decrypt(String operatorName, Object object, String charset) throws CryptoException;


    /**
     * 恢复明文
     *
     * @param plainTextList 缓存的明文列表
     */
    void recoveryPlainText(List<ObjectPlainText> plainTextList);
}
