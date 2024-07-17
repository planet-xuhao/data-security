package com.rhf.common.security.spring.autoconfigure;

import com.rhf.common.security.config.DataSecurityProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Properties;

/**
 * 属性配置加载
 *
 * @author xuh
 * @date 2024/7/17
 */
public class DataSecurityPropertiesLoader {

    private static final String CONFIG_PATH = "data-security.properties";

    public DataSecurityProperties load() throws IOException {
        Properties properties = new Properties();
        Resource resource = new ClassPathResource(CONFIG_PATH);
        // 资源不存在返回默认配置
        if (!resource.exists()) {
            return new DataSecurityProperties();
        }
        properties.load(resource.getInputStream());
        return new DataSecurityProperties(properties);
    }
}
