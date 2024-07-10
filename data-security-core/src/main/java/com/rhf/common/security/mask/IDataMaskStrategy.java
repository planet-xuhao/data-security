package com.rhf.common.security.mask;

/**
 * 脱敏策略
 *
 * @author xuh
 * @date 2024/7/7
 */
public interface IDataMaskStrategy {

    /**
     * 对数据进行脱敏
     *
     * @param data    待脱敏数据
     * @param context 脱敏信息上下文
     * @return 脱敏后数据
     */
    String mask(String data, IMaskContext context);


    /**
     * 返回脱敏策略名称
     *
     * @return 脱敏策略名称
     */
    String getType();
}
