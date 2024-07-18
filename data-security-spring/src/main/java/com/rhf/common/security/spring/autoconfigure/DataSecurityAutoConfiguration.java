package com.rhf.common.security.spring.autoconfigure;

import com.rhf.common.security.config.DataSecurityProperties;
import com.rhf.common.security.spring.autoconfigure.condition.DataSecurityCondition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.IOException;

/**
 * 加解密/脱敏组件自动装配
 *
 * @author xuh
 * @date 2024/7/17
 */
@Configuration
@Import({DataSecurityEncryptConfiguration.class, DataSecurityMaskConfiguration.class, DataSecurityAopConfiguration.class})
@Conditional(DataSecurityCondition.class)
public class DataSecurityAutoConfiguration {

    @Bean
    public DataSecurityProperties dataSecurityProperties() throws IOException {
        return new DataSecurityPropertiesLoader().load();
    }
}
