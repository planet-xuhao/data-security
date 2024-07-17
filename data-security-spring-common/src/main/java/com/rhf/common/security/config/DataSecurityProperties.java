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
    private DataSecurityEncryptProperties encryptProperties;

    /**
     * 脱敏配置
     */
    private DataSecurityMaskProperties maskProperties;

    public DataSecurityProperties() {
    }

    public DataSecurityProperties(Properties properties) {
        this.enabled = Boolean.parseBoolean(properties.getProperty(PREFIX + ".enabled", "true"));
        this.maskProperties = new DataSecurityMaskProperties(properties);
        this.encryptProperties = new DataSecurityEncryptProperties(properties);
    }
}
