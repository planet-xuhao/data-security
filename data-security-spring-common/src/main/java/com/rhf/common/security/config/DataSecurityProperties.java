package com.rhf.common.security.config;

import lombok.Getter;
import lombok.Setter;

import java.util.Properties;

/**
 * @author xuh
 * @date 2024/7/11
 */
@Getter
@Setter
public class DataSecurityProperties {

    public static final String PREFIX = "spring.data-security";

    /**
     * 开启整个组件
     */
    private boolean enabled;

    /**
     * 加密配置
     */
    private DataSecurityEncryptProperties encrypt;

    /**
     * 脱敏配置
     */
    private DataSecurityMaskProperties mask;

    /**
     * springboot自动装配
     */
    public DataSecurityProperties() {
    }

    /**
     * spring方式
     */
    public DataSecurityProperties(Properties properties) {
        this.enabled = Boolean.parseBoolean(properties.getProperty(PREFIX + ".enabled", "true"));
        this.mask = new DataSecurityMaskProperties(properties);
        this.encrypt = new DataSecurityEncryptProperties(properties);
    }
}
