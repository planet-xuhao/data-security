package com.rhf.common.security.mask.fixture;

import com.rhf.common.security.mask.annotation.MaskCondition;
import com.rhf.common.security.mask.annotation.strategy.MaskChineseName;
import lombok.Getter;
import lombok.Setter;

/**
 * @author xuh
 * @date 2024/7/10
 */
@Getter
@Setter
public class FieldConditionObj {

    @MaskCondition(NameCondition.class)
    @MaskChineseName
    private String name;
}