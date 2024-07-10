package com.rhf.common.security.mask;

import java.lang.reflect.Field;

/**
 * 脱敏上下文
 *
 * @author xuh
 * @date 2024/7/7
 */
public interface IMaskContext {
    /**
     * 获取当前要脱敏的对象实例
     *
     * @return 实例对象
     */
    Object getInstance();

    /**
     * 获取脱敏字段
     *
     * @return 字段
     */
    Field getField();
}
