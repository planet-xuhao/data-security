package com.rhf.common.security.crypto.annotation;

import java.lang.annotation.*;

/**
 * 对方法返回结果解密
 *
 * @author xuh
 * @date 2024/7/17
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DecryptBody {

}
