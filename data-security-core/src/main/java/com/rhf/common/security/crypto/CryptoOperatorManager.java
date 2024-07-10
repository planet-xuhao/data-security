package com.rhf.common.security.crypto;

import com.rhf.common.security.crypto.exception.EncryptException;
import com.rhf.common.security.crypto.operator.ICryptoOperator;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 加密算法管理器
 *
 * @author xuh
 * @date 2024/7/5
 */
public class CryptoOperatorManager {

    @Getter
    @Setter
    private String primaryOperator;
    private final Map<String, ICryptoOperator> operatorMap = new HashMap<>();

    public synchronized void registerStrategy(ICryptoOperator cryptoOperator) {
        if (cryptoOperator == null) {
            throw new IllegalArgumentException("crypto operator is null.");
        }
        String name = cryptoOperator.getType();
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("crypto operator name is null.");
        }
        operatorMap.put(name, cryptoOperator);
    }

    public ICryptoOperator getOperator(String name) {
        ICryptoOperator cryptoOperator;
        if (name == null || name.isEmpty()) {
            cryptoOperator = operatorMap.get(primaryOperator);
        } else {
            cryptoOperator = operatorMap.get(name);
        }

        if (cryptoOperator == null) {
            throw new EncryptException("crypto operator is not register");
        }
        return cryptoOperator;
    }
}
