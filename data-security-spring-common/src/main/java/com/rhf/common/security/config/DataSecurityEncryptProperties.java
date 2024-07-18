package com.rhf.common.security.config;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * 加密相关属性配置
 *
 * @author xuh
 * @date 2024/7/10
 */
@Getter
@Setter
public class DataSecurityEncryptProperties {

    /**
     * 是否加载启用
     */
    private boolean enabled = true;

    /**
     * 需要排除的类/包，通过字符最长匹配实现
     */
    private List<String> excludes;

    /**
     * 切面优先级
     */
    private Integer aopOrder;

    /**
     * 加密算法配置
     */
    private List<CryptoOperatorProperties> operators;

    public DataSecurityEncryptProperties() {

    }

    public DataSecurityEncryptProperties(Properties properties) {
        this.enabled = Boolean.parseBoolean(properties.getProperty(DataSecurityProperties.PREFIX + ".encrypt.enabled", "true"));
        this.aopOrder = Integer.parseInt(properties.getProperty(DataSecurityProperties.PREFIX + ".encrypt.aop-order", "0"));
        // 解析所有的加密算法
        String operatorPrefix = DataSecurityProperties.PREFIX + ".encrypt.operators";
        // 读取所有的指定前缀的配置
        Set<String> indexList = new HashSet<>();
        for (Object key : properties.keySet()) {
            String k = key.toString();
            // 获得所有加密相关配置
            if (k.startsWith(operatorPrefix)) {
                // 可能的值是spring.data-security.encrypt.operators[0].type
                // 需要只保留[0],其余部分需要移除,目的是为了获取后续所有[0]的值
                String index = k.replace(operatorPrefix, "");
                index = index.substring(0, index.indexOf("."));
                indexList.add(index);
            }
        }

        this.operators = new ArrayList<>();
        // 获取index下的所有配置
        for (String index : indexList) {
            // 前缀是spring.data-security.encrypt.operators + 数组下标
            // 例如: spring.data-security.encrypt.operators[0]
            String prefix = operatorPrefix + index;
            Properties sub = new Properties();
            for (Object key : properties.keySet()) {
                String k = key.toString();
                if (k.startsWith(prefix)) {
                    // 将所有同类的配置值全放到一个新的properties中，如所有的[0]的值，并去掉前缀
                    // 例如：spring.data-security.encrypt.operators[0].type
                    // 处理后： type
                    String subKey = k.replace(prefix + ".", "");
                    sub.put(subKey, properties.get(key));
                }
            }

            // 注册所有的加密算法
            CryptoOperatorProperties operatorProperties = new CryptoOperatorProperties(sub);
            this.operators.add(operatorProperties);
        }
    }
}
