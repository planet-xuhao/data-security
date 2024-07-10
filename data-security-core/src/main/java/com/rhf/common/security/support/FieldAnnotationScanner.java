package com.rhf.common.security.support;

import com.rhf.common.security.utils.AnnotationUtils;
import com.rhf.common.security.utils.JdkTypeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对象注解扫描工具，该工具将扫描类中所有的属性的属性，对于属性是类的也会进行深度扫描
 *
 * @author xuh
 * @date 2024/7/5
 */
public class FieldAnnotationScanner<T extends Annotation> {

    /**
     * 存储一个类对应的，可继续向下进行深度扫描的字段。
     * 该字段必定不能是基础类型，包装类型，字符串，只能是对象类型，而且是非jdk对象
     */
    private final Map<Class<?>, List<Field>> canDeepScanFieldMap = new ConcurrentHashMap<>();

    /**
     * 缓存一个类上拥有目标注解的字段，加快反射调用，减少逐个字段访问
     */
    private final Map<Class<?>, List<AnnotationFieldCache<T>>> targetAnnotationFieldMap = new ConcurrentHashMap<>();

    /**
     * 需要扫描的注解类型
     */
    private final Class<T> annotationType;

    /**
     * 需要扫描的属性类类型
     */
    private final List<Class<?>> supportClassList;

    public FieldAnnotationScanner(Class<T> annotationType, List<Class<?>> supportClassList) {
        this.annotationType = annotationType;
        this.supportClassList = supportClassList;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class AnnotationFieldCache<T> {
        /**
         * 所标注的字段
         */
        private final Field field;
        /**
         * 字段上的注解
         */
        private final T annotation;
    }

    public interface ScanResultHandler<T> {
        void handle(Object object, Field field, T annotation) throws IllegalAccessException;
    }

    public void scanAndHandle(Object object, ScanResultHandler<T> handler)
            throws IllegalAccessException {
        Set<Integer> visited = new HashSet<>();
        Queue<Object> queue = new LinkedList<>();
        queue.add(object);
        do {
            Object current = queue.poll();
            if (current == null) {
                continue;
            }
            Class<?> clazz = current.getClass();
            // 基本类型不做处理
            if (JdkTypeUtils.isPrimitiveOrWrapper(clazz)) {
                continue;
            }
            // 扫描数组
            if (clazz.isArray()) {
                scanArray(current, queue);
                continue;
            }
            // 扫描集合
            if (Collection.class.isAssignableFrom(clazz)) {
                scanCollection(current, queue);
                continue;
            }
            // 扫描map结构
            if (Map.class.isAssignableFrom(clazz)) {
                scanMap(current, queue);
                continue;
            }
            // 跳过java和javax的类型，这两类不做任何扫描
            if (JdkTypeUtils.isJdkCoreClass(clazz)) {
                // jdk和jdk相关的拓展包不需要进行向下扫描
                continue;
            }
            // 普通对象的情况下需要计算对象的hashCode，避免重复判断
            int hashCode = System.identityHashCode(current);
            if (!visited.add(hashCode)) {
                continue;
            }
            // 扫描对象
            scanObject(current, queue, handler);
        } while (!queue.isEmpty());
    }

    /**
     * 扫描数组，将数组中符合条件的每一个元素都加入队列
     */
    private void scanArray(Object current, Queue<Object> scanQueue) {
        int length = Array.getLength(current);
        for (int i = 0; i < length; i++) {
            Object item = Array.get(current, i);
            if (skipElementType(item)) {
                continue;
            }
            scanQueue.add(item);
        }
    }

    /**
     * 扫描集合，将集合中符合条件的每一个元素都加入队列
     */
    private void scanCollection(Object current, Queue<Object> scanQueue) {
        // 如果是集合，将集合中的每个元素都加入到队列中
        Collection<?> collection = (Collection<?>) current;
        for (Object obj : collection) {
            if (skipElementType(obj)) {
                continue;
            }
            scanQueue.add(obj);
        }
    }

    /**
     * 扫描Map，将Map符合条件的每一个元素都加入队列
     */
    private void scanMap(Object current, Queue<Object> scanQueue) {
        Map<?, ?> map = (Map<?, ?>) current;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (skipElementType(entry.getValue())) {
                continue;
            }
            scanQueue.add(entry.getValue());
        }
    }

    /**
     * 扫描对象，检查属性上的注解信息
     *
     * @param current   待扫描的对象
     * @param scanQueue 扫描队列
     * @param handler   扫描结果处理器
     */
    private void scanObject(Object current, Queue<Object> scanQueue, ScanResultHandler<T> handler)
            throws IllegalAccessException {
        // 从缓存中获取含有注解的字段进行快速扫描
        Class<?> clazz = current.getClass();
        List<AnnotationFieldCache<T>> annotationFieldCacheList = targetAnnotationFieldMap.get(clazz);
        List<Field> deepScanList = canDeepScanFieldMap.get(clazz);
        if (deepScanList == null) {
            // 初始化对象的属性信息，解析注解
            initFieldCache(clazz);
            annotationFieldCacheList = targetAnnotationFieldMap.get(clazz);
            deepScanList = canDeepScanFieldMap.get(clazz);
        }

        // 获取对象的注解字段
        for (AnnotationFieldCache<T> cache : annotationFieldCacheList) {
            handler.handle(current, cache.getField(), cache.getAnnotation());
        }

        // 继续向下进行深度扫描
        for (Field field : deepScanList) {
            Object value = field.get(current);
            if (value != null) {
                scanQueue.add(value);
            }
        }
    }


    /**
     * 判断对象是否需要被解析
     *
     * @param obj 对象
     * @return ture 跳过
     */
    private boolean skipElementType(Object obj) {
        if (obj == null) {
            return false;
        }
        return !isNeedDeepScan(null, obj.getClass());
    }

    /**
     * 如果该对象属性上没有注解，需要检查是否符合深度扫描的条件
     * 1. 不是基本类型/包装类型
     * 2. 不是jdk对象类型
     * 3. 不是String类型
     *
     * @param fieldGenericType 字段类型
     * @param clazz            类对象类型
     * @return true 需要扫描
     */
    private boolean isNeedDeepScan(Type fieldGenericType, Class<?> clazz) {
        // 检查是不是泛型参数，如果是泛型参数则继续扫描
        if (fieldGenericType instanceof TypeVariable) {
            return true;
        }

        // 容器类型都可以继续向下扫描
        if (clazz.isArray() || Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz)) {
            return true;
        }

        return !JdkTypeUtils.isPrimitiveOrWrapper(clazz) && !JdkTypeUtils.isJdkCoreClass(clazz);
    }


    /**
     * 初始化类缓存
     * 1. 初始化一个类上拥有目标注解的字段
     * 2. 缓存一个类上哪些字段能够向下扫描
     *
     * @param clazz 类缓存
     */
    private void initFieldCache(Class<?> clazz) {
        // 缓存注解字段
        LinkedList<AnnotationFieldCache<T>> cacheList = new LinkedList<>();
        LinkedList<Field> deepScanList = new LinkedList<>();
        List<Field> allField = getFields(clazz);
        for (Field field : allField) {
            Type fieldGenericType = field.getGenericType();
            Class<?> fieldClassType = field.getType();
            // 设置访问权限
            if (isSupportClass(fieldGenericType, fieldClassType)) {
                field.setAccessible(true);
                T anno = AnnotationUtils.findAnnotation(field, annotationType);
                // 如果含有注解，那么就不需要进行深度扫描了
                if (anno != null) {
                    cacheList.add(new AnnotationFieldCache<>(field, anno));
                    continue;
                }
            }

            // 判断是否为深层的加密注解
            if (isNeedDeepScan(fieldGenericType, fieldClassType)) {
                field.setAccessible(true);
                deepScanList.add(field);
            }
        }

        if (cacheList.isEmpty()) {
            targetAnnotationFieldMap.put(clazz, Collections.emptyList());
        } else {
            targetAnnotationFieldMap.put(clazz, cacheList);
        }
        if (deepScanList.isEmpty()) {
            canDeepScanFieldMap.put(clazz, Collections.emptyList());
        } else {
            canDeepScanFieldMap.put(clazz, deepScanList);
        }
    }

    /**
     * 获取一个对象上的所有字段，包含父类的字段
     *
     * @param clazz 操作对象
     * @return 所有字段集合
     */
    private List<Field> getFields(Class<?> clazz) {
        ArrayList<Field> fieldList = new ArrayList<>();
        do {
            Field[] fieldArr = clazz.getDeclaredFields();
            fieldList.addAll(Arrays.asList(fieldArr));
            clazz = clazz.getSuperclass();
        } while (clazz != null);

        return fieldList;
    }

    /**
     * 是否支持扫描该对象类型上的注解
     *
     * @param fieldTGenericType 属性类型
     * @param classType         类类型
     * @return true 支持
     */
    private boolean isSupportClass(Type fieldTGenericType, Class<?> classType) {
        // 泛型无法知道具体类型，默认支持
        if (fieldTGenericType instanceof TypeVariable) {
            return true;
        }
        if (supportClassList != null) {
            for (Class<?> supportClass : supportClassList) {
                // 判断当前的类与支持的类是否是同一个或者为其父类
                if (supportClass.isAssignableFrom(classType)) {
                    return true;
                }
            }
        }
        return false;
    }
}
