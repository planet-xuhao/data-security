package com.rhf.common.security.mask.annotation.strategy;

import java.lang.annotation.*;

/**
 * 用于标注在对应的方法上，表示该方法会对返回结果进行脱敏
 *
 * @author xuh
 * @date 2024/7/9
 */
@Documented
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaskMethod {
}