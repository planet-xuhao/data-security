package com.rhf.common.security.spring.boot.autoconfigure;

import com.rhf.common.security.config.DataSecurityProperties;
import com.rhf.common.security.initizer.MaskStrategyManagerInitializer;
import com.rhf.common.security.mask.DefaultDataMaskSpec;
import com.rhf.common.security.mask.IDataMaskSpec;
import com.rhf.common.security.mask.IDataMaskStrategy;
import com.rhf.common.security.mask.MaskStrategyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 脱敏配置
 *
 * @author xuh
 * @date 2024/7/17
 */
public class DataSecurityMaskConfiguration {

    private final DataSecurityProperties dataSecurityProperties;

    public DataSecurityMaskConfiguration(DataSecurityProperties dataSecurityProperties) {
        this.dataSecurityProperties = dataSecurityProperties;
    }

    /**
     * 配置脱敏算法
     * @param strategies 自定义脱敏算法，并注册为Bean
     * @return MaskStrategyManager
     */
    @Bean
    @ConditionalOnMissingBean
    public MaskStrategyManager maskStrategyManager(@Autowired(required = false) List<IDataMaskStrategy> strategies) {
        return new MaskStrategyManagerInitializer(dataSecurityProperties, strategies).init();
    }

    @Bean
    public IDataMaskSpec dataMaskSpec(MaskStrategyManager maskStrategyManager) {
        return new DefaultDataMaskSpec(maskStrategyManager);
    }
}
