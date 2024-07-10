package com.rhf.common.security.mask;

/**
 * 数据脱敏接口
 *
 * @author xuh
 * @date 2024/7/7
 */
public interface IDataMaskSpec {

    /**
     * 数据脱敏
     *
     * @param data     待脱敏数据
     * @param strategy 脱敏策略
     * @return 脱敏后数据
     */
    String mask(String data, String strategy);

    /**
     * 对象脱敏
     *
     * @param object 待脱敏对象
     */
    void mask(Object object);
}
