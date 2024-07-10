package com.rhf.common.security.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 注解工具类
 *
 * @author xuh
 * @date 2024/7/6
 */
public class AnnotationUtils {

    private static final String JAVA_ANNOTATION_PKG = "java";

    public static <T extends Annotation> T findAnnotation(Annotation annotation, Class<T> annotationClazz) {
        if (annotationClazz == null) {
            return null;
        }
        // 检查该注解是不是就是预期的注解
        if (annotation.annotationType().equals(annotationClazz)) {
            return (T) annotation;
        } else {
            // 过滤掉java自身注解，避免死循环
            Class<? extends Annotation> clazz = annotation.annotationType();
            if (clazz.getPackage().getName().startsWith(JAVA_ANNOTATION_PKG)) {
                return null;
            }
            //对复合注解进行处理
            Annotation[] annotations = annotation.annotationType().getDeclaredAnnotations();
            for (Annotation anno : annotations) {
                T result = findAnnotation(anno, annotationClazz);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    public static <T extends Annotation> T findAnnotation(Field field, Class<T> annotationClazz) {
        Annotation[] annotations = field.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            T result = findAnnotation(annotation, annotationClazz);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public static <T extends Annotation> boolean hasAnnotation(Field field, Class<T> annotationClazz) {
        return findAnnotation(field, annotationClazz) != null;
    }

    public static <T extends Annotation> T findAnnotation(Method method, Class<T> annotationClazz) {
        Annotation[] annotations = method.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            T result = findAnnotation(annotation, annotationClazz);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public static <T extends Annotation> boolean hasAnnotation(Method method, Class<T> annotationClazz) {
        return findAnnotation(method, annotationClazz) != null;
    }

    public static <T extends Annotation> T findAnnotation(Class<?> type, Class<T> annotationClazz) {
        Annotation[] annotations = type.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            T result = findAnnotation(annotation, annotationClazz);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

}
