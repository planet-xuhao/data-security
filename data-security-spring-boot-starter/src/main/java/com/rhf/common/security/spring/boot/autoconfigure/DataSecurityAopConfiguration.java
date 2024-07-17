package com.rhf.common.security.spring.boot.autoconfigure;

import com.rhf.common.security.config.DataSecurityProperties;
import com.rhf.common.security.config.GlobalMaskCondition;
import com.rhf.common.security.crypto.IDataEncryptSpec;
import com.rhf.common.security.initizer.EncryptAspectInitializer;
import com.rhf.common.security.initizer.MaskAspectInitializer;
import com.rhf.common.security.mask.IDataMaskSpec;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;

import java.util.List;

/**
 * 切面配置
 *
 * @author xuh
 * @date 2024/7/17
 */
public class DataSecurityAopConfiguration {

    @Autowired
    private DataSecurityProperties dataSecurityProperties;

    // 配置加密切面

    @Bean(name = "dataEncryptAdvisor")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(name = "dataEncryptAdvisor")
    @ConditionalOnProperty(prefix = DataSecurityProperties.PREFIX + ".encrypt", name = "enabled", havingValue = "true", matchIfMissing = true)
    public Advisor dataEncryptAdvisor(@Autowired IDataEncryptSpec dataEncryptSpec) {
        return new EncryptAspectInitializer(dataSecurityProperties, dataEncryptSpec).init();
    }


    /**
     * 脱敏切面只对返回结果进行处理，为了保证能够正常将数据进行加密放到对应的加密字段，然后再将原始字段进行脱敏，所以脱敏的切面一定要高优先级，这样才能在做完所有处理后再对结果进行脱敏
     * <p>
     * Mask AOP      -------------------------<p>
     * Encrypt AOP   -------------------------<p>
     * normal HANDLE -------------------------<p>
     *
     * @param dataMaskSpec 脱敏组件
     * @return 返回脱敏切面配置
     */
    @Bean(name = "dataMaskAdvisor")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(name = "dataMaskAdvisor")
    @ConditionalOnProperty(prefix = DataSecurityProperties.PREFIX + ".mask", name = "enabled", havingValue = "true", matchIfMissing = true)
    public Advisor dataMaskAdvisor(@Autowired IDataMaskSpec dataMaskSpec,
                                   @Autowired(required = false) List<GlobalMaskCondition> globalMaskConditions) {
        return new MaskAspectInitializer(dataSecurityProperties, dataMaskSpec, globalMaskConditions).init();
    }
}
