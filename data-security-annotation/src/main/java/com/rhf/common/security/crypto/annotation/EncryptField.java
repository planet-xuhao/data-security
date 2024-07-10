package com.rhf.common.security.crypto.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注在字段上，表示需要加密
 *
 * @author xuh
 * @date 2024/7/7
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EncryptField {

    /**
     * 加密时：指将该字段的值加密到同对象的哪个具体的字段上，提供值的字段不做变化
     * 解密时：指将该字段的值从加密变为原始字段并赋值给该字段
     *
     * @return 字段名
     */
    String targetField() default "";

    /**
     * 在进行加密时，如果存在指定字段，那么在解密时是否要还原到指定原字段
     *
     * @return true 还原
     */
    boolean isOverride() default true;
}
