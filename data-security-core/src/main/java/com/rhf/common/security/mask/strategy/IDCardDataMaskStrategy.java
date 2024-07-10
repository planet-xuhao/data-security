package com.rhf.common.security.mask.strategy;


import com.rhf.common.security.mask.IMaskContext;
import com.rhf.common.security.mask.exception.DataMaskException;

/**
 * Description
 * 身份证脱敏策略
 * <p>
 * 超过8位：保留前6位和后2位
 * 小于8位：保留前1位和最后1位
 *
 * @author xuh
 * @date 2024/7/7
 */
public class IDCardDataMaskStrategy extends AbstractDataMaskStrategy {
    @Override
    public String mask(String data, IMaskContext context) throws DataMaskException {
        if (data.length() <= 8) {
            return rangeMask(data, 1, 1);
        } else {
            return rangeMask(data, 6, 2);
        }
    }

    @Override
    public String getType() {
        return "ID_CARD";
    }
}
