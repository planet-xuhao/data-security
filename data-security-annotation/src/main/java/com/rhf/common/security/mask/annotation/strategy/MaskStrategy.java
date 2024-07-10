package com.rhf.common.security.mask.annotation.strategy;

import java.lang.annotation.*;

/**
 * 脱敏策略
 *
 * @author xuh
 * @date 2024/7/7
 */
@Documented
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaskStrategy {

    /**
     * 脱敏策略
     *
     * @return 策略名称
     */
    String value() default "DEFAULT";
}
