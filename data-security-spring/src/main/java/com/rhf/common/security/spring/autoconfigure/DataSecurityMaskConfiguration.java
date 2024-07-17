package com.rhf.common.security.spring.autoconfigure;

import com.rhf.common.security.config.DataSecurityProperties;
import com.rhf.common.security.initizer.MaskStrategyManagerInitializer;
import com.rhf.common.security.mask.DefaultDataMaskSpec;
import com.rhf.common.security.mask.IDataMaskSpec;
import com.rhf.common.security.mask.IDataMaskStrategy;
import com.rhf.common.security.mask.MaskStrategyManager;
import com.rhf.common.security.spring.autoconfigure.condition.DataSecurityMaskCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 脱敏相关Bean配置
 *
 * @author xuh
 * @date 2024/7/17
 */
@Configuration
@Conditional({DataSecurityMaskCondition.class})
public class DataSecurityMaskConfiguration {

    /**
     * 兼容4.1.0的spring框架，不采用构造器注入方式
     */
    @Autowired
    private DataSecurityProperties dataSecurityProperties;

    // 配置脱敏算子
    @Bean
    public MaskStrategyManager maskStrategyManager(@Autowired(required = false) List<IDataMaskStrategy> strategies) {
        return new MaskStrategyManagerInitializer(dataSecurityProperties, strategies).init();
    }

    @Bean
    public IDataMaskSpec dataMaskSpec(MaskStrategyManager maskStrategyManager) {
        return new DefaultDataMaskSpec(maskStrategyManager);
    }
}
