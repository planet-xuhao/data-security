package com.rhf.common.security.mask.fixture;

import com.rhf.common.security.mask.condition.IMaskCondition;

/**
 * @author xuh
 * @date 2024/7/10
 */
public class ObjectCondition implements IMaskCondition {
    @Override
    public boolean condition(Object obj) {
        if (!(obj instanceof ConditionObj)) {
            return false;
        }
        ConditionObj conditionObj = (ConditionObj) obj;
        return !conditionObj.getName().contains("张");
    }
}
