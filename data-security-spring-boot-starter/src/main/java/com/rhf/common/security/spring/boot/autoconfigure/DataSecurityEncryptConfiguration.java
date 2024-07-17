package com.rhf.common.security.spring.boot.autoconfigure;

import com.rhf.common.security.config.DataSecurityProperties;
import com.rhf.common.security.crypto.CryptoOperatorManager;
import com.rhf.common.security.crypto.DefaultDataEncryptSpec;
import com.rhf.common.security.crypto.IDataEncryptSpec;
import com.rhf.common.security.crypto.operator.ICryptoOperator;
import com.rhf.common.security.initizer.CryptoOperatorManagerInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 加密功能相关配置
 *
 * @author xuh
 * @date 2024/7/17
 */
public class DataSecurityEncryptConfiguration {

    private final DataSecurityProperties dataSecurityProperties;

    public DataSecurityEncryptConfiguration(DataSecurityProperties dataSecurityProperties) {
        this.dataSecurityProperties = dataSecurityProperties;
    }

    /**
     * 配置加密算法
     *
     * @param cryptoOperators 自定义加密算法，并注册为Bean
     * @return CryptoOperatorManager
     */
    @Bean
    @ConditionalOnMissingBean
    public CryptoOperatorManager cryptoOperatorManager(@Autowired(required = false) List<ICryptoOperator> cryptoOperators) {
        return new CryptoOperatorManagerInitializer(dataSecurityProperties, cryptoOperators).init();
    }

    @Bean
    @ConditionalOnMissingBean
    public IDataEncryptSpec dataEncryptSpec(CryptoOperatorManager cryptoOperatorManager) {
        return new DefaultDataEncryptSpec(cryptoOperatorManager);
    }

}
