package com.rhf.common.security.spring.autoconfigure.condition;

import com.rhf.common.security.config.DataSecurityProperties;
import com.rhf.common.security.crypto.exception.CryptoException;
import com.rhf.common.security.spring.autoconfigure.DataSecurityPropertiesLoader;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.io.IOException;

/**
 * 加解密/脱敏组件启用条件
 *
 * @author xuh
 * @date 2024/7/17
 */
public class DataSecurityCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        DataSecurityProperties dataSecurityProperties;
        try {
            dataSecurityProperties = new DataSecurityPropertiesLoader().load();
        } catch (IOException e) {
            throw new CryptoException("加载加密配置文件异常", e);
        }
        // 判断是否启用加解密/脱敏组件
        return dataSecurityProperties.isEnabled();
    }
}
