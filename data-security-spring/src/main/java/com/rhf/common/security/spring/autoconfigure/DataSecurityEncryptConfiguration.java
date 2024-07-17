package com.rhf.common.security.spring.autoconfigure;

import com.rhf.common.security.config.DataSecurityProperties;
import com.rhf.common.security.crypto.CryptoOperatorManager;
import com.rhf.common.security.crypto.DefaultDataEncryptSpec;
import com.rhf.common.security.crypto.IDataEncryptSpec;
import com.rhf.common.security.crypto.operator.ICryptoOperator;
import com.rhf.common.security.initizer.CryptoOperatorManagerInitializer;
import com.rhf.common.security.spring.autoconfigure.condition.DataSecurityEncryptCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 加密功能相关Bean配置
 *
 * @author xuh
 * @date 2024/7/17
 */
@Configuration
@Conditional(DataSecurityEncryptCondition.class)
public class DataSecurityEncryptConfiguration {

    /**
     * 兼容4.1.0的spring框架，不采用构造器注入方式
     */
    @Autowired
    private DataSecurityProperties dataSecurityProperties;

    /**
     * 读取配置文件创建加密器
     */
    @Bean
    public CryptoOperatorManager cryptoOperatorManager(@Autowired(required = false) List<ICryptoOperator> cryptoOperators) {
        return new CryptoOperatorManagerInitializer(dataSecurityProperties, cryptoOperators).init();
    }

    /**
     * 配置加密算法
     */
    @Bean
    public IDataEncryptSpec dataEncryptSpec(CryptoOperatorManager cryptoOperatorManager) {
        return new DefaultDataEncryptSpec(cryptoOperatorManager);
    }
}
