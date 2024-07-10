package com.rhf.common.security.mask.annotation.strategy;

import java.lang.annotation.*;

/**
 * 卡号脱敏
 *
 * @author xuh
 * @date 2024/7/7
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MaskStrategy("ID_CARD")
public @interface MaskIDCard {
}
