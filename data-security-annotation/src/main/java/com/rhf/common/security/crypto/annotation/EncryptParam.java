package com.rhf.common.security.crypto.annotation;

import java.lang.annotation.*;

/**
 * 加密参数，用于标注在方法参数上，表示该字段需要加密
 *
 * @author xuh
 * @date 2024/7/16
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EncryptParam {
}
