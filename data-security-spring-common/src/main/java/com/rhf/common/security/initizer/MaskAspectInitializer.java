package com.rhf.common.security.initizer;

import com.rhf.common.security.aop.DataSecurityAdvisor;
import com.rhf.common.security.aop.mask.DataSecurityMaskMethodAdvice;
import com.rhf.common.security.config.DataSecurityProperties;
import com.rhf.common.security.config.GlobalMaskCondition;
import com.rhf.common.security.mask.IDataMaskSpec;
import com.rhf.common.security.mask.annotation.strategy.MaskMethod;
import org.aopalliance.aop.Advice;
import org.springframework.core.Ordered;

import java.util.List;

/**
 * 脱敏切面初始化
 *
 * @author xuh
 * @date 2024/7/17
 */
public class MaskAspectInitializer {
    private final DataSecurityProperties dataSecurityProperties;

    private final IDataMaskSpec dataMaskSpec;

    private final List<GlobalMaskCondition> globalMaskConditions;

    public MaskAspectInitializer(DataSecurityProperties dataSecurityProperties, IDataMaskSpec dataMaskSpec,
                                 List<GlobalMaskCondition> globalMaskConditions) {
        this.dataSecurityProperties = dataSecurityProperties;
        this.dataMaskSpec = dataMaskSpec;
        this.globalMaskConditions = globalMaskConditions;
    }

    public DataSecurityAdvisor init() {
        Advice advice = new DataSecurityMaskMethodAdvice(dataSecurityProperties, dataMaskSpec, globalMaskConditions);
        DataSecurityAdvisor advisor = new DataSecurityAdvisor(advice, MaskMethod.class);
        // 设置优先级
        if (dataSecurityProperties != null
                && dataSecurityProperties.getMaskProperties() != null
                && dataSecurityProperties.getMaskProperties().getAopOrder() != null) {
            advisor.setOrder(dataSecurityProperties.getMaskProperties().getAopOrder());
        } else {
            advisor.setOrder(Ordered.HIGHEST_PRECEDENCE);
        }
        return advisor;
    }
}