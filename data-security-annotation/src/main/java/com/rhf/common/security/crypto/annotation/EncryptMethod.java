package com.rhf.common.security.crypto.annotation;

import java.lang.annotation.*;

/**
 * 标注在方法上，表示该方法的请求或响应需要加密/解密
 *
 * @author xuh
 * @date 2024/7/16
 */
@Documented
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EncryptMethod {

    /**
     * 加密算法
     *
     * @return 加密算法名称
     */
    String value() default "";
}
