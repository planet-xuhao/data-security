package com.rhf.common.security.initizer;

import com.rhf.common.security.config.CryptoOperatorProperties;
import com.rhf.common.security.config.DataSecurityEncryptProperties;
import com.rhf.common.security.config.DataSecurityProperties;
import com.rhf.common.security.crypto.CryptoOperatorManager;
import com.rhf.common.security.crypto.exception.CryptoException;
import com.rhf.common.security.crypto.operator.ICryptoOperator;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Properties;

/**
 * 加密算法初始化工具
 *
 * @author xuh
 * @date 2024/7/16
 */
@Slf4j
public class CryptoOperatorManagerInitializer {

    private final DataSecurityProperties dataSecurityProperties;

    private final List<ICryptoOperator> cryptoOperatorList;

    public CryptoOperatorManagerInitializer(DataSecurityProperties dataSecurityProperties, List<ICryptoOperator> cryptoOperatorList) {
        this.dataSecurityProperties = dataSecurityProperties;
        this.cryptoOperatorList = cryptoOperatorList;
    }

    public CryptoOperatorManager init() {
        CryptoOperatorManager cryptoOperatorManager = new CryptoOperatorManager();
        // 注册配置文件中配置的加密算法
        try {
            this.registerOperatorFormProperties(cryptoOperatorManager);
        } catch (Exception e) {
            log.error("register operator from properties failed, exit system!", e);
            System.exit(-1);
        }
        // 注册代码中预定义的，可能会覆盖配置文件中的
        if (cryptoOperatorList != null) {
            cryptoOperatorList.forEach(cryptoOperatorManager::registerStrategy);
        }
        // 检查是否存在主加密算法
        if (cryptoOperatorManager.getPrimaryOperator() == null) {
            log.error("no primary operator found, exit system!");
            System.exit(-1);
        }
        return cryptoOperatorManager;
    }

    /**
     * 加密算法注册到CryptoOperatorManager
     */
    private void registerOperatorFormProperties(CryptoOperatorManager cryptoOperatorManager) {
        DataSecurityEncryptProperties encryptProperties = dataSecurityProperties.getEncrypt();
        if (encryptProperties == null) {
            return;
        }
        List<CryptoOperatorProperties> operatorPropertiesList = encryptProperties.getOperators();
        if (operatorPropertiesList == null) {
            return;
        }
        for (CryptoOperatorProperties cryptoOperatorProperties : operatorPropertiesList) {
            Class<? extends ICryptoOperator> type = cryptoOperatorProperties.getType();
            if (type == null) {
                throw new CryptoException("operator type is null");
            }
            //  实例化对象
            ICryptoOperator cryptoOperator;
            try {
                // 使用带属性的有参构造方法
                Properties properties = cryptoOperatorProperties.getProps() == null ? new Properties() : cryptoOperatorProperties.getProps();
                Constructor<? extends ICryptoOperator> constructor = type.getConstructor(Properties.class);
                cryptoOperator = constructor.newInstance(properties);
                cryptoOperatorManager.registerStrategy(cryptoOperator);
            } catch (Exception e) {
                throw new CryptoException("init operator failed, type:" + type.getName(), e);
            }
            if (cryptoOperatorProperties.isPrimary()) {
                cryptoOperatorManager.setPrimaryOperator(cryptoOperator.getType());
            }
        }
    }
}
