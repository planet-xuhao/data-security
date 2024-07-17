package com.rhf.common.security.initizer;

import com.rhf.common.security.aop.DataSecurityAdvisor;
import com.rhf.common.security.aop.crypto.DataSecurityEncryptMethodAdvice;
import com.rhf.common.security.config.DataSecurityProperties;
import com.rhf.common.security.crypto.IDataEncryptSpec;
import com.rhf.common.security.crypto.annotation.EncryptMethod;
import org.aopalliance.aop.Advice;

/**
 * 加密切面初始化
 *
 * @author xuh
 * @date 2024/7/16
 */
public class EncryptAspectInitializer {

    private final DataSecurityProperties dataSecurityProperties;

    private final IDataEncryptSpec dataEncryptSpec;

    public EncryptAspectInitializer(DataSecurityProperties dataSecurityProperties, IDataEncryptSpec dataEncryptSpec) {
        this.dataSecurityProperties = dataSecurityProperties;
        this.dataEncryptSpec = dataEncryptSpec;
    }

    public DataSecurityAdvisor init() {
        Advice advice = new DataSecurityEncryptMethodAdvice(dataSecurityProperties, dataEncryptSpec);
        DataSecurityAdvisor advisor = new DataSecurityAdvisor(advice, EncryptMethod.class);
        if (dataSecurityProperties != null && dataSecurityProperties.getEncryptProperties() != null
                && dataSecurityProperties.getEncryptProperties().getAopOrder() != null) {
            advisor.setOrder(dataSecurityProperties.getEncryptProperties().getAopOrder());
        }
        return advisor;
    }
}
