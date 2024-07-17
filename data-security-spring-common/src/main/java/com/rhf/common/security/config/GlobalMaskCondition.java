package com.rhf.common.security.config;

import java.lang.reflect.Method;

/**
 * 全局脱敏条件，用于决策方法是否继续进行脱敏处理
 *
 * @author xuh
 * @date 2024/7/17
 */
public interface GlobalMaskCondition {

    /**
     * 脱敏条件
     *
     * @param method 判断方法
     * @param arguments 方法参数
     * @return true：进行脱敏
     */
    boolean condition(Method method, Object[] arguments);
}
