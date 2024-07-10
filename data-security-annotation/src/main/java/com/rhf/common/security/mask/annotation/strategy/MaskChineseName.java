package com.rhf.common.security.mask.annotation.strategy;

import java.lang.annotation.*;

/**
 * 姓名脱敏注解
 *
 * @author xuh
 * @date 2024/7/7
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MaskStrategy("CHINESE_NAME")
public @interface MaskChineseName {
}
