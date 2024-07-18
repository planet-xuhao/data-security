package com.rhf.common.security.aop.crypto;

import com.rhf.common.security.config.DataSecurityEncryptProperties;
import com.rhf.common.security.config.DataSecurityMaskProperties;
import com.rhf.common.security.config.DataSecurityProperties;
import com.rhf.common.security.crypto.IDataEncryptSpec;
import com.rhf.common.security.crypto.ObjectPlainText;
import com.rhf.common.security.crypto.annotation.*;
import com.rhf.common.security.utils.AnnotationUtils;
import com.rhf.common.security.utils.JdkTypeUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通知（增强）：加密方法处理
 *
 * @author xuh
 * @date 2024/7/16
 */
@Slf4j
public class DataSecurityEncryptMethodAdvice implements MethodInterceptor {

    @Setter
    @Getter
    private DataSecurityProperties dataSecurityProperties;

    private final IDataEncryptSpec dataEncryptSpec;

    /**
     * 方法上注解缓存，避免重复解析
     */
    private final Map<Method, MethodCache> methodCacheMap = new ConcurrentHashMap<>();

    public DataSecurityEncryptMethodAdvice(DataSecurityProperties dataSecurityProperties, IDataEncryptSpec dataEncryptSpec) {
        this.dataEncryptSpec = dataEncryptSpec;
        this.setDataSecurityProperties(dataSecurityProperties);
    }

    @Getter
    @Setter
    private static class MethodCache {
        // 方法加密注解
        private EncryptMethod encryptMethod;

        // 脱敏加密注解
        private MaskEncryptedMethod maskEncryptedMethod;

        // 加密参数注解
        private EncryptParam[] encryptParamArr;

        // 解密参数注解
        private DecryptParam[] decryptParamArr;

        // 是否需要加密返回值
        private boolean hasDecryptBody;

        // 是否需要加密返回值
        private boolean hasEncryptBody;

        private String operatorName;

    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        String className = method.getDeclaringClass().getName();
        // debug模式下打印处理方法名和类名
        if (log.isDebugEnabled()) {
            log.debug("encrypt-method invoke method: {}, class: {}", method.getName(), className);
        }
        // 如果加密方法需要做排除，那么不做后续加解密处理
        if (isExclude(className)) {
            return invocation.proceed();
        }
        MethodCache methodCache = this.getMethodCache(method);
        String operatorName = methodCache.getOperatorName();
        List<ObjectPlainText> plainTextList = null;
        Object invokeResult;
        try {
            // 参数处理
            plainTextList = this.handleArguments(operatorName, methodCache, invocation.getArguments());
            // 真实方法
            invokeResult = invocation.proceed();
        } finally {
            // 加密后，内存中的对象应该是原本的样子，还原对象
            if (plainTextList != null && !plainTextList.isEmpty()) {
                dataEncryptSpec.recoveryPlainText(plainTextList);
            }
        }

        return this.handleReturn(operatorName, methodCache, invokeResult);
    }

    private boolean isExclude(String className) {
        DataSecurityEncryptProperties encryptProperties = dataSecurityProperties.getEncrypt();
        if (encryptProperties != null && encryptProperties.getExcludes() != null) {
            for (String exclude : encryptProperties.getExcludes()) {
                if (exclude != null && !exclude.isEmpty() && className.startsWith(exclude)) {
                    return true;
                }
            }
        }
        return false;
    }

    private MethodCache getMethodCache(Method method) {
        MethodCache methodCache = methodCacheMap.get(method);
        if (methodCache == null) {
            // 解析方法,初始化缓存
            parseAndCacheMethod(method);
        }
        return methodCacheMap.get(method);
    }

    private void parseAndCacheMethod(Method method) {
        MethodCache methodCache = new MethodCache();
        // 获取EncryptMethod注解
        methodCache.setEncryptMethod(AnnotationUtils.findAnnotation(method, EncryptMethod.class));
        // 获取MaskEncryptedMethod注解
        methodCache.setMaskEncryptedMethod(AnnotationUtils.findAnnotation(method, MaskEncryptedMethod.class));
        // 获取参数注解
        // 检查参数上是否有加密参数的注解
        int paramCount = method.getParameterCount();
        methodCache.setEncryptParamArr(new EncryptParam[paramCount]);
        EncryptParam[] encryptParamArr = methodCache.getEncryptParamArr();

        // 校验参数上是否有解密注解
        methodCache.setDecryptParamArr(new DecryptParam[paramCount]);
        DecryptParam[] decryptParamArr = methodCache.getDecryptParamArr();

        for (int i = 0; i < paramCount; i++) {
            Annotation[] parameterAnnotations = method.getParameterAnnotations()[i];
            for (Annotation annotation : parameterAnnotations) {
                EncryptParam encryptParam = AnnotationUtils.findAnnotation(annotation, EncryptParam.class);
                if (encryptParam != null) {
                    encryptParamArr[i] = encryptParam;
                }

                DecryptParam decryptParam = AnnotationUtils.findAnnotation(annotation, DecryptParam.class);
                if (decryptParam != null) {
                    decryptParamArr[i] = decryptParam;
                }
            }
        }

        // 扫描返回值注解
        methodCache.setHasEncryptBody(AnnotationUtils.hasAnnotation(method, EncryptBody.class));
        methodCache.setHasDecryptBody(AnnotationUtils.hasAnnotation(method, DecryptBody.class));

        // 确认加密算法名称
        methodCache.setOperatorName(this.getOperatorName(methodCache));

        // 添加到缓存中
        methodCacheMap.put(method, methodCache);
    }

    /**
     * 获取加密策略
     *
     * @param methodCache 方法缓存
     * @return 加密策略名
     */
    private String getOperatorName(MethodCache methodCache) {
        // 检查方法上是否存在脱敏和加密的混合注解
        MaskEncryptedMethod maskEncryptedMethod = methodCache.getMaskEncryptedMethod();
        String operatorName = null;
        if (maskEncryptedMethod != null) {
            DataSecurityMaskProperties maskProperties = dataSecurityProperties.getMask();
            if (maskProperties != null) {
                operatorName = maskProperties.getEncryptOperatorName();
                if (operatorName != null && !operatorName.isEmpty()) {
                    return operatorName;
                }
            }
        }

        // 获取方法上的加密注解
        EncryptMethod encryptMethod = methodCache.getEncryptMethod();
        if (encryptMethod != null) {
            operatorName = encryptMethod.value();
        }
        return operatorName;
    }

    /**
     * 处理加密参数，将待加密参数转化为ObjectPlainText对象
     *
     * @param operatorName 加密算法
     * @param methodCache  methodCache
     * @param arguments    待加密参数
     * @return ObjectPlainText集合
     */
    private List<ObjectPlainText> handleArguments(String operatorName, MethodCache methodCache, Object[] arguments) {
        int length = arguments.length;
        List<ObjectPlainText> plainTextList = new LinkedList<>();
        for (int index = 0; index < length; index++) {
            if (methodCache.getEncryptParamArr()[index] != null) {
                // 加密参数
                plainTextList.addAll(encryptParameters(index, operatorName, arguments));
                continue;
            }
            if (Objects.nonNull(methodCache.getDecryptParamArr()[index])) {
                decryptParameters(index, operatorName, arguments);
            }
        }
        return plainTextList;
    }

    /**
     * 参数加密
     *
     * @param index        参数下标
     * @param operatorName 加密算法
     * @param arguments    参数列表
     * @return ObjectPlainText集合
     */
    private List<ObjectPlainText> encryptParameters(int index, String operatorName, Object[] arguments) {
        Object param = arguments[index];
        if (param == null) {
            return Collections.emptyList();
        }

        // 如果是基本类型，不做处理
        if (JdkTypeUtils.isPrimitiveOrWrapper(param.getClass())) {
            return Collections.emptyList();
        }

        // 字符串的时候做参数替换
        if (param instanceof String str) {
            arguments[index] = dataEncryptSpec.encrypt(operatorName, str);
            return Collections.emptyList();
        }

        // 对对象类型做加密，这种情况无需替换
        return dataEncryptSpec.encrypt(operatorName, param);
    }

    /**
     * 对参数对象做解密
     *
     * @param index        参数下标
     * @param operatorName 加密算法
     * @param arguments    参数列表
     */
    private void decryptParameters(int index, String operatorName, Object[] arguments) {
        Object param = arguments[index];
        if (param == null) {
            return;
        }
        // 如果是基础类型，就不做处理
        if (JdkTypeUtils.isPrimitiveOrWrapper(param.getClass())) {
            return;
        }

        // 字符串的时候做参数替换
        if (param instanceof String stringParam) {
            arguments[index] = dataEncryptSpec.decrypt(operatorName, stringParam);
            return;
        }

        // 对对象类型做加密，这种情况无需替换
        dataEncryptSpec.decrypt(operatorName, param);
    }

    /**
     * 返回结果处理
     *
     * @param operatorName 加密算法
     * @param methodCache  methodCache
     * @param invokeResult 真实方法调用的返回结果
     * @return 处理后的对象
     */
    private Object handleReturn(String operatorName, MethodCache methodCache, Object invokeResult) {
        if (Objects.isNull(invokeResult)) {
            return null;
        }
        // 如果是基础类型，就不做处理
        if (JdkTypeUtils.isPrimitiveOrWrapper(invokeResult.getClass())) {
            return invokeResult;
        }
        // 检查是否需要对响应结果进行解密
        if (methodCache.hasDecryptBody) {
            return decryptReturn(operatorName, invokeResult);
        }
        // 检查是否需要对响应结果进行加密
        if (methodCache.hasEncryptBody) {
            return encryptReturn(operatorName, invokeResult);
        }
        return invokeResult;
    }

    private Object encryptReturn(String operatorName, Object invokeResult) {
        // 获取方法上的加密注解

        if (invokeResult instanceof String result) {
            return dataEncryptSpec.encrypt(operatorName, result);
        }

        // 如果返回类型是Collection，并且泛型是String，那么就对每个元素进行加密
        if (invokeResult instanceof Collection<?> collection) {
            if (collection.isEmpty()) {
                return invokeResult;
            }
            Object o = collection.iterator().next();
            if (o instanceof String) {
                List<String> resultList = new ArrayList<>(collection.size());
                for (Object item : collection) {
                    resultList.add(dataEncryptSpec.encrypt(operatorName, (String) item));
                }
                return resultList;
            }
        }

        // 以上条件均不符合，直接对对象进行解密
        dataEncryptSpec.encrypt(operatorName, invokeResult);
        return invokeResult;
    }

    private Object decryptReturn(String operatorName, Object invokeResult) {
        // 如果是基础类型，就不做处理
        if (invokeResult instanceof String result) {
            return dataEncryptSpec.decrypt(operatorName, result);
        }

        // 如果返回类型是Collection，并且泛型是String，那么就对每个元素进行解密
        if (invokeResult instanceof Collection<?> collection) {
            if (collection.isEmpty()) {
                return invokeResult;
            }
            Object o = collection.iterator().next();
            if (o instanceof String) {
                List<String> resultList = new ArrayList<>(collection.size());
                for (Object item : collection) {
                    resultList.add(dataEncryptSpec.decrypt(operatorName, (String) item));
                }
                return resultList;
            }
        }

        // 以上条件均不符合，直接对对象进行解密
        dataEncryptSpec.decrypt(operatorName, invokeResult);
        return invokeResult;
    }
}
