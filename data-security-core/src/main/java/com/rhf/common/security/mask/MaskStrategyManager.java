package com.rhf.common.security.mask;

import com.rhf.common.security.mask.IDataMaskStrategy;
import com.rhf.common.security.mask.strategy.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 脱敏策略管理器，用户维护内部和外部注册的脱敏策略
 * @author xuh
 * @date 2024/7/7
 */
public class MaskStrategyManager {

    private final Map<String, IDataMaskStrategy> strategyMap = new HashMap<>();

    public MaskStrategyManager() {
        // 注册内建的策略
        this.registerStrategy(new CardNoDataMaskStrategy());
        this.registerStrategy(new IDCardDataMaskStrategy());
        this.registerStrategy(new NameDataMaskStrategy());
        this.registerStrategy(new PhoneDataMaskStrategy());
        this.registerStrategy(new DefaultDataStrategy());
        this.registerStrategy(new CustomRangeMaskStrategy());
        this.registerStrategy(new EmailDataMaskStrategy());
        this.registerStrategy(new CarNumDataMaskStrategy());
    }

    public synchronized void registerStrategy(IDataMaskStrategy maskStrategy) {
        if (maskStrategy == null) {
            throw new IllegalArgumentException("mask strategy is null.");
        }
        String maskTypeName = maskStrategy.getType();
        if (maskTypeName == null || maskTypeName.isEmpty()) {
            throw new IllegalArgumentException("mask strategy name is null.");
        }
        strategyMap.put(maskTypeName, maskStrategy);
    }

    public IDataMaskStrategy getStrategy(String maskTypeName) {
        return strategyMap.get(maskTypeName);
    }
}
