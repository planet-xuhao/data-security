package com.rhf.common.security.mask;

/**
 * 用于不存在配置，但是还需要注入该bean的情况下使用。这种情况下不会做任务的脱敏动作
 *
 * @author xuh
 * @date 2024/7/7
 */
public class EmptyDataMaskSpec implements IDataMaskSpec {
    @Override
    public String mask(String data, String strategy) {
        return data;
    }

    @Override
    public void mask(Object object) {
        //不做对象脱敏
    }
}
