package com.rhf.common.security.config;

import com.rhf.common.security.crypto.exception.CryptoException;
import com.rhf.common.security.mask.IDataMaskStrategy;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 脱敏相关属性配置
 *
 * @author xuh
 * @date 2024/7/11
 */
@Getter
@Setter
public class DataSecurityMaskProperties {

    /**
     * 是否开启脱敏
     */
    private boolean enabled = true;

    /**
     * 切面优先级
     */
    private Integer aopOrder;

    /**
     * 自定义的脱敏规则
     */
    private List<Class<? extends IDataMaskStrategy>> startegyList;

    /**
     * 加密算法名字，如果没有配置时使用
     */
    private String encryptOperatorName;

    public DataSecurityMaskProperties() {

    }

    public DataSecurityMaskProperties(Properties properties) {
        this.enabled = Boolean.parseBoolean(properties.getProperty(DataSecurityProperties.PREFIX + ".mask.enabled", "true"));
        this.aopOrder = Integer.parseInt(properties.getProperty(DataSecurityProperties.PREFIX + ".mask.aop-order", "0"));
        this.encryptOperatorName = properties.getProperty(DataSecurityProperties.PREFIX + ".mask.encrypt-operator-name");

        // 解析所有的脱敏算法
        String strategyPrefix = DataSecurityProperties.PREFIX + ".mask.strategies";
        // 读取所有的指定前缀的配置
        List<String> indexList = new ArrayList<>();
        for (Object key : properties.keySet()) {
            String k = key.toString();
            if (k.startsWith(strategyPrefix)) {
                indexList.add(k.replace(strategyPrefix, ""));
            }
        }

        // 加载配置中的脱敏规则
        this.startegyList = new ArrayList<>();
        for (String key : indexList) {
            String className = properties.getProperty(strategyPrefix + key);
            if (className == null) {
                continue;
            }
            try {
                Class<?> clazz = Class.forName(className);
                // 判断是否为脱敏实现
                if (IDataMaskStrategy.class.isAssignableFrom(clazz)) {
                    this.startegyList.add((Class<? extends IDataMaskStrategy>) clazz);
                } else {
                    throw new CryptoException("class " + className + " is not a subclass of IDataMaskStrategy!");
                }
            } catch (ClassNotFoundException e) {
                throw new CryptoException(e);
            }
        }
    }
}