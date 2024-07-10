package com.rhf.common.security.crypto.operator;

import lombok.Getter;
import lombok.Setter;

import java.util.Properties;

/**
 * @author xuh
 * @date 2024/7/5
 */
@Getter
@Setter
public abstract class AbstractOperator implements ICryptoOperator {
    private final String algorithmName;

    private final Properties properties;

    public AbstractOperator(Properties properties, String algorithmName) {
        this.properties = properties;
        this.algorithmName = algorithmName;
    }

    @Override
    public String getType() {
        return algorithmName;
    }
}
