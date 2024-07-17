package com.rhf.common.security.crypto.annotation;

import java.lang.annotation.*;

/**
 * 解密参数注解，用于标注在方法的参数上，表示该参数入参时需要做解密
 *
 * @author xuh
 * @date 2024/7/16
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DecryptParam {

}
