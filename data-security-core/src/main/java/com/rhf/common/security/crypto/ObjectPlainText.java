package com.rhf.common.security.crypto;

import lombok.Getter;

import java.lang.reflect.Field;

/**
 * 对象明文，存储了对象和对象中要加密的明文字段
 *
 * @author xuh
 * @date 2024/7/5
 */
@Getter
public class ObjectPlainText {

    /**
     * 对象
     */
    private final Object object;

    /**
     * 待加密字段明文
     */
    private final String plainText;

    /**
     * 待加密属性
     */
    private final Field field;

    public ObjectPlainText(String plainText, Object object, Field field) {
        this.plainText = plainText;
        this.object = object;
        this.field = field;
    }

}
