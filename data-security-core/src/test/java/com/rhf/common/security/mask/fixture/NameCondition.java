package com.rhf.common.security.mask.fixture;

import com.rhf.common.security.mask.condition.IMaskCondition;

/**
 * @author xuh
 * @date 2024/7/10
 */
public class NameCondition implements IMaskCondition {
    @Override
    public boolean condition(Object obj) {
        if (!(obj instanceof String)) {
            return false;
        }
        String value = (String) obj;
        return !value.contains("张");
    }
}
