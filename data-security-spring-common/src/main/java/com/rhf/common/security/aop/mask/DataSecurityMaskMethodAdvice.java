package com.rhf.common.security.aop.mask;

import com.rhf.common.security.config.DataSecurityProperties;
import com.rhf.common.security.config.GlobalMaskCondition;
import com.rhf.common.security.mask.IDataMaskSpec;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * 脱敏方法增强
 *
 * @author xuh
 * @date 2024/7/17
 */
@Slf4j
public class DataSecurityMaskMethodAdvice implements MethodInterceptor {

    @Getter
    @Setter
    private DataSecurityProperties dataSecurityProperties;

    private final IDataMaskSpec dataMaskSpec;

    private final List<GlobalMaskCondition> globalMaskConditions;

    public DataSecurityMaskMethodAdvice(DataSecurityProperties dataSecurityProperties,
                                        IDataMaskSpec dataMaskSpec,
                                        List<GlobalMaskCondition> globalMaskConditions) {
        this.dataMaskSpec = dataMaskSpec;
        this.globalMaskConditions = globalMaskConditions;
        this.setDataSecurityProperties(dataSecurityProperties);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        //  debug模式下打印处理的方法名和类名
        Method method = invocation.getMethod();
        String className = method.getDeclaringClass().getName();
        if (log.isDebugEnabled()) {
            log.debug("mask-method invoke method: {}, class: {}", method.getName(), className);
        }
        // 先调用方法得到返回结果
        Object result = invocation.proceed();
        if (Objects.nonNull(globalMaskConditions)) {
            boolean isNeedMask = false;
            for (GlobalMaskCondition condition : globalMaskConditions) {
                if (condition.condition(invocation.getMethod(), invocation.getArguments())) {
                    isNeedMask = true;
                    break;
                }
            }
            // 无需脱敏直接返回结果
            if (!isNeedMask) {
                return result;
            }
        }

        // 检查方法上是否存在
        if (result != null) {
            dataMaskSpec.mask(result);
        }
        return result;
    }
}
