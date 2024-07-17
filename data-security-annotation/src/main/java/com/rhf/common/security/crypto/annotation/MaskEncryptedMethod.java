package com.rhf.common.security.crypto.annotation;

import com.rhf.common.security.mask.annotation.strategy.MaskMethod;

import java.lang.annotation.*;

/**
 * 脱敏加密方法，标注了该注解的方法将能够对请求参数和响应进行脱敏并加密
 *
 * @author xuh
 * @date 2024/7/17
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@EncryptMethod
@MaskMethod
public @interface MaskEncryptedMethod {
}

