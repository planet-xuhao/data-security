package com.rhf.common.security.mask.condition;

/**
 * 脱敏条件
 * 当满足脱敏条件时，脱敏注解才生效
 *
 * @author xuh
 * @date 2024/7/7
 */
public interface IMaskCondition {

    /**
     * 脱敏条件
     *
     * @param obj 判断对象
     * @return true：进行脱敏
     */
    boolean condition(Object obj);
}
