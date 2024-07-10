package com.rhf.common.security.mask.annotation.strategy;

import java.lang.annotation.*;

/**
 * 范围脱敏
 *
 * @author xuh
 * @date 2024/7/7
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MaskStrategy("RANGE")
public @interface MaskRange {

    /**
     * 从多少位开始脱敏
     *
     * @return 位置
     */
    int startLen();

    /**
     * 从多少位结束
     *
     * @return 位置
     */
    int afterLen();
}
