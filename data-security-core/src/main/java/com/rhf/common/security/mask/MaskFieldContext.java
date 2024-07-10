package com.rhf.common.security.mask;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;

/**
 * 对字段进行脱敏上下文信息
 *
 * @author xuh
 * @date 2024/7/7
 */
@Setter
@Getter
public class MaskFieldContext implements IMaskContext {

    private Object instance;

    private Field field;

    @Override
    public Object getInstance() {
        return instance;
    }

    @Override
    public Field getField() {
        return field;
    }

    public MaskFieldContext() {
    }

    public MaskFieldContext(Object instance, Field field) {
        this.instance = instance;
        this.field = field;
    }
}
