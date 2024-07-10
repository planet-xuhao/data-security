package com.rhf.common.security.mask.condition;

/**
 * @author xuh
 * @date 2024/7/7
 */
public class AlwaysTrueCondition implements IMaskCondition {
    @Override
    public boolean condition(Object obj) {
        return true;
    }
}
