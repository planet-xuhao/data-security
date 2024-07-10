package com.rhf.common.security.mask.annotation.strategy;

import java.lang.annotation.*;

/**
 * 通用卡号脱敏注解
 *
 * @author xuh
 * @date 2024/7/7
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MaskStrategy("CARD_NO")
public @interface MaskCardNo {
}
