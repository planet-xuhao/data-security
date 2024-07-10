package com.rhf.common.security.mask;

import com.rhf.common.security.mask.annotation.MaskCondition;
import com.rhf.common.security.mask.condition.IMaskCondition;
import com.rhf.common.security.mask.annotation.strategy.MaskStrategy;
import com.rhf.common.security.mask.exception.DataMaskException;
import com.rhf.common.security.support.FieldAnnotationScanner;
import com.rhf.common.security.utils.AnnotationUtils;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xuh
 * @date 2024/7/7
 */
public class DefaultDataMaskSpec implements IDataMaskSpec {
    @Setter
    private MaskStrategyManager maskStrategyManager;

    /**
     * 默认情况下的条件注解
     */
    private final Class<? extends IMaskCondition>[] DEFAULT_CONDITION_ARR = new Class[0];

    /**
     * 缓存脱敏条件Class和它的实列，避免重复反射创建对象
     */
    private final Map<Class<? extends IMaskCondition>, IMaskCondition> conditionInstanceMap = new ConcurrentHashMap<>();

    /**
     * 记录对象拥有哪些条件注解
     */
    private final Map<Class<?>, Class<? extends IMaskCondition>[]> classConditionMap = new ConcurrentHashMap<>();

    /**
     * 记录属性拥有哪些条件注解
     */
    private final Map<Field, Class<? extends IMaskCondition>[]> fieldConditionMap = new ConcurrentHashMap<>();

    private final FieldAnnotationScanner<MaskStrategy> fieldScanner =
            new FieldAnnotationScanner<>(MaskStrategy.class, Collections.singletonList(String.class));


    public DefaultDataMaskSpec(MaskStrategyManager maskStrategyManager) {
        this.maskStrategyManager = maskStrategyManager;
    }

    @Override
    public String mask(String data, String strategy) {
        return mask(data, strategy, null);
    }

    @Override
    public void mask(Object object) {
        List<IMaskCondition> conditionList = new ArrayList<>();
        // 对字段进行扫描脱敏
        try {
            fieldScanner.scanAndHandle(object, (instance, field, annotation) -> {
                MaskFieldContext maskContext = new MaskFieldContext(instance, field);
                conditionList.clear();

                // 进行类脱敏条件注解判断
                if (isCurrentObjectNeedMask(conditionList, instance)) {
                    Object value = field.get(instance);
                    if (isCurrentFieldNeedMask(conditionList, field, value)) {
                        // 获取对应的脱敏策略对字符串数据进行脱敏
                        String maskValue = mask((String) value, annotation.value(), maskContext);
                        field.set(instance, maskValue);
                    }
                }
            });
        } catch (IllegalAccessException e) {
            throw new DataMaskException(e);
        }
    }

    public String mask(String data, String maskTypeName, IMaskContext maskContext) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        if (maskTypeName == null || maskTypeName.isEmpty()) {
            throw new DataMaskException("invalid mask strategy!");
        }

        IDataMaskStrategy maskStrategy = this.maskStrategyManager.getStrategy(maskTypeName);
        if (maskStrategy == null) {
            throw new DataMaskException("no found mask strategy <" + maskTypeName + ">!");
        }
        return maskStrategy.mask(data, maskContext);
    }

    /**
     * 判断对象是否需要进行脱敏（判断类上是否存在脱敏条件注解）
     *
     * @param conditionList 脱敏条件结果
     * @param instance      待判断对象
     * @return true：需要脱敏
     */
    private boolean isCurrentObjectNeedMask(List<IMaskCondition> conditionList, Object instance) {
        List<IMaskCondition> objectConditionList = this.findClassCondition(conditionList, instance.getClass());
        return isNeedMask(objectConditionList, instance);
    }

    private boolean isCurrentFieldNeedMask(List<IMaskCondition> conditionList, Field field, Object value) {
        if (Objects.isNull(value)) {
            return false;
        }
        // 进行字段上的脱敏注解条件判断
        List<IMaskCondition> fieldConditionList = this.findFieldCondition(conditionList, field);
        return isNeedMask(fieldConditionList,value);
    }

    private boolean isNeedMask(List<IMaskCondition> conditionList, Object value) {
        // 最开始的地方new了conditionList
        if (conditionList.isEmpty()) {
            return true;
        }
        boolean isNeedMask = false;
        for (IMaskCondition condition : conditionList) {
            if (condition.condition(value)) {
                isNeedMask = true;
                break;
            }
        }
        conditionList.clear();
        return isNeedMask;
    }

    /**
     * 从对象类上获取条件注解
     *
     * @param conditionList 条件结果集合
     * @param objType       对象类型
     * @return 如果存在注解，则返回对应条件集合
     */
    private List<IMaskCondition> findClassCondition(List<IMaskCondition> conditionList, Class<?> objType) {
        Class<? extends IMaskCondition>[] conditionClassArr = classConditionMap.get(objType);
        if (conditionClassArr == null) {
            MaskCondition conditionAnno = AnnotationUtils.findAnnotation(objType, MaskCondition.class);
            if (conditionAnno != null) {
                conditionClassArr = conditionAnno.value();
            } else {
                conditionClassArr = DEFAULT_CONDITION_ARR;
            }
            // 记录对象拥有哪些条件注解
            classConditionMap.put(objType, conditionClassArr);
        }
        return this.getCondition(conditionList, conditionClassArr);
    }

    /**
     * 从属性上获取条件注解
     *
     * @param conditionList 条件结果集合
     * @param field         属性
     * @return 如果属性上存在注解，则返回对应条件集合
     */
    private List<IMaskCondition> findFieldCondition(List<IMaskCondition> conditionList, Field field) {
        Class<? extends IMaskCondition>[] conditionClassArr = fieldConditionMap.get(field);
        if (Objects.isNull(conditionClassArr)) {
            MaskCondition conditionAnno = AnnotationUtils.findAnnotation(field, MaskCondition.class);
            if (Objects.nonNull(conditionAnno)) {
                conditionClassArr = conditionAnno.value();
            } else {
                conditionClassArr = DEFAULT_CONDITION_ARR;
            }
            fieldConditionMap.put(field, conditionClassArr);
        }
        return this.getCondition(conditionList, conditionClassArr);
    }

    private List<IMaskCondition> getCondition(List<IMaskCondition> conditionList, Class<? extends IMaskCondition>[] conditionClassArr) {
        for (Class<? extends IMaskCondition> clazz : conditionClassArr) {
            IMaskCondition instance = conditionInstanceMap.get(clazz);
            if (instance == null) {
                try {
                    instance = clazz.newInstance();
                } catch (Exception e) {
                    throw new DataMaskException("create condition instance failed", e);
                }
                conditionInstanceMap.put(clazz, instance);
            }
            conditionList.add(instance);
        }
        return conditionList;
    }


}


