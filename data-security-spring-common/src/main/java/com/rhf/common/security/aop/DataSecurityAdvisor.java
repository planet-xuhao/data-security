package com.rhf.common.security.aop;

import com.rhf.common.security.utils.AnnotationUtils;
import org.aopalliance.aop.Advice;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.StaticMethodMatcher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import java.io.Serial;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 高级通知：通知 + 切点
 *
 * @author xuh
 * @date 2024/7/16
 */
public class DataSecurityAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

    @Serial
    private static final long serialVersionUID = -3579532926016572754L;

    private final transient Pointcut pointcut;

    private final transient Advice advice;

    public DataSecurityAdvisor(Advice advice, Class<? extends Annotation> annotationType) {
        this.advice = advice;
        this.pointcut = new AnnotationMethodPoint(annotationType);
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        // Spring 容器在创建该 bean 时会调用 setBeanFactory 方法，可以将beanFactory赋值给advice
        if (this.advice instanceof BeanFactoryAware) {
            ((BeanFactoryAware) this.advice).setBeanFactory(beanFactory);
        }
    }


    private record AnnotationMethodPoint(Class<? extends Annotation> annotationType) implements Pointcut {

        @Override
            public ClassFilter getClassFilter() {
                return ClassFilter.TRUE;
            }

            @Override
            public MethodMatcher getMethodMatcher() {
                return new AnnotationMethodMatcher(annotationType);
            }

            /**
             * 定义匹配方式
             */
            private static class AnnotationMethodMatcher extends StaticMethodMatcher {

                private final Class<? extends Annotation> annotationType;

                public AnnotationMethodMatcher(Class<? extends Annotation> annotationType) {
                    this.annotationType = annotationType;
                }

                @Override
                public boolean matches(Method method, Class<?> targetClass) {
                    if (matchesMethod(method)) {
                        return true;
                    }
                    if (Proxy.isProxyClass(targetClass)) {
                        return false;
                    }
                    Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);
                    return specificMethod != method && matchesMethod(specificMethod);
                }

                private boolean matchesMethod(Method method) {
                    return AnnotationUtils.hasAnnotation(method, this.annotationType);
                }
            }
        }
}
