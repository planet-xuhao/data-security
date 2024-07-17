package com.rhf.common.security.config;

import com.rhf.common.security.crypto.exception.CryptoException;
import com.rhf.common.security.crypto.operator.ICryptoOperator;
import lombok.Getter;
import lombok.Setter;

import java.util.Properties;

/**
 * 加密算法配置
 *
 * @author xuh
 * @date 2024/7/10
 */
@Getter
@Setter
public class CryptoOperatorProperties {

    /**
     * 判断是否为默认加密算法
     */
    private boolean primary = false;

    /**
     * 加密算法实现类型
     */
    private Class<? extends ICryptoOperator> type;

    /**
     * 算法自身属性
     */
    private Properties props;

    public CryptoOperatorProperties() {
    }

    public CryptoOperatorProperties(Properties properties) {
        this.primary = Boolean.parseBoolean(properties.getProperty("primary", "false"));
        String type = properties.getProperty("type");
        try {
            this.type = (Class<? extends ICryptoOperator>) Class.forName(type);
        } catch (ClassNotFoundException e) {
            throw new CryptoException("无法找到指定的加密算法实现类：" + type, e);
        }

        // 读取所有的指定前缀的配置
        this.props = new Properties();
        String prefix = "props.";
        for (Object key : properties.keySet()) {
            String k = key.toString();
            if (k.contains(prefix)) {
                // 只保留数组下标
                String propKey = k.replace(prefix, "");
                this.props.put(propKey, properties.getProperty(k));
            }
        }
    }
}
