package com.rhf.common.security.spring.boot.autoconfigure;

import com.rhf.common.security.config.DataSecurityProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 自动装配
 *
 * @author xuh
 * @date 2024/7/17
 */
@Configuration
@Import({DataSecurityEncryptConfiguration.class, DataSecurityMaskConfiguration.class, DataSecurityAopConfiguration.class})
@ConditionalOnProperty(prefix = DataSecurityProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class DataSecurityAutoConfiguration {

    @ConfigurationProperties(prefix = DataSecurityProperties.PREFIX)
    @Bean
    public DataSecurityProperties dataSecurityProperties() {
        return new DataSecurityProperties();
    }
}
