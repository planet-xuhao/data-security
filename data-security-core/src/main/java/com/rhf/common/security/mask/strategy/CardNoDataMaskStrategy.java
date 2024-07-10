package com.rhf.common.security.mask.strategy;


import com.rhf.common.security.mask.IMaskContext;
import com.rhf.common.security.mask.exception.DataMaskException;

/**
 * Description
 * 卡号脱敏规则
 * <p>
 * 保留第一位和后四位，其余脱敏
 *
 * @author xuh
 * @date 2024/7/7
 */
public class CardNoDataMaskStrategy extends AbstractDataMaskStrategy {
    @Override
    public String mask(String data, IMaskContext context) throws DataMaskException {
        return rangeMask(data, 1, 4);
    }

    @Override
    public String getType() {
        return "CARD_NO";
    }
}
