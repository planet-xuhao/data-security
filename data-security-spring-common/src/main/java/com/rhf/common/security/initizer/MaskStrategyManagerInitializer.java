package com.rhf.common.security.initizer;

import com.rhf.common.security.config.DataSecurityMaskProperties;
import com.rhf.common.security.config.DataSecurityProperties;
import com.rhf.common.security.mask.IDataMaskStrategy;
import com.rhf.common.security.mask.MaskStrategyManager;
import com.rhf.common.security.mask.exception.DataMaskException;

import java.util.List;

/**
 * 脱敏算法初始化
 * 引入这个的目的是为了解决后续存在不同版本的spring项目，注入条件方式的判断控制
 *
 * @author xuh
 * @date 2024/7/11
 */
public class MaskStrategyManagerInitializer {

    private final DataSecurityProperties dataSecurityProperties;

    private final List<IDataMaskStrategy> strategyList;

    public MaskStrategyManagerInitializer(DataSecurityProperties dataSecurityProperties, List<IDataMaskStrategy> strategyList) {
        this.dataSecurityProperties = dataSecurityProperties;
        this.strategyList = strategyList;
    }

    public MaskStrategyManager init() {
        MaskStrategyManager maskStrategyManager = new MaskStrategyManager();
        // 注册配置文件中的
        DataSecurityMaskProperties maskProperties = dataSecurityProperties.getMaskProperties();
        if (maskProperties != null) {
            List<Class<? extends IDataMaskStrategy>> configList = maskProperties.getStartegyList();
            if (configList != null) {
                for (Class<? extends IDataMaskStrategy> ele : configList) {
                    try {
                        IDataMaskStrategy strategy = ele.newInstance();
                        maskStrategyManager.registerStrategy(strategy);
                    } catch (Exception e) {
                        throw new DataMaskException("init mask strategy " + ele.getName()
                                + " error, please check config file", e);
                    }
                }
            }
        }
        // 注册自定义的脱敏策略
        if (strategyList != null) {
            strategyList.forEach(maskStrategyManager::registerStrategy);
        }
        return maskStrategyManager;
    }
}
