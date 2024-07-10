package com.rhf.common.security.mask.annotation;

import com.rhf.common.security.mask.condition.AlwaysTrueCondition;
import com.rhf.common.security.mask.condition.IMaskCondition;

import java.lang.annotation.*;

/**
 * @author xuh
 * @date 2024/7/9
 */
@Documented
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaskCondition {
    Class<? extends IMaskCondition>[] value() default {AlwaysTrueCondition.class};
}
