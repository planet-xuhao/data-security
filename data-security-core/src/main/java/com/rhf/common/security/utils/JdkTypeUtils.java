package com.rhf.common.security.utils;

/**
 * java类型判断
 *
 * @author xuh
 * @date 2024/7/6
 */
public class JdkTypeUtils {

    /**
     * 判断该类是否是基本类型或包装类
     *
     * @param clazz 类型
     * @return true 是
     */
    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || clazz == Integer.class || clazz == Short.class ||
                clazz == Long.class || clazz == Float.class || clazz == Double.class ||
                clazz == Character.class || clazz == Byte.class || clazz == Boolean.class;
    }

    /**
     * 判断改类是否为jdk的核心类或拓展类
     *
     * @param type 类型
     * @return true 是
     */
    public static boolean isJdkCoreClass(Class<?> type) {
        Package packageInfo = type.getPackage();
        if (packageInfo == null) {
            return true;
        }
        String packageName = packageInfo.getName();
        return (packageName.startsWith("javax.") || packageName.startsWith("java."));
    }
}
