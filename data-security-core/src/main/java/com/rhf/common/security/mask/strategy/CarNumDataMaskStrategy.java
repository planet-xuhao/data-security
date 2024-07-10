package com.rhf.common.security.mask.strategy;


import com.rhf.common.security.mask.IMaskContext;
import com.rhf.common.security.mask.exception.DataMaskException;

/**
 * Description
 * 车牌号脱敏策略
 * 从第二位开始，到倒数第二位
 *
 * @author xuh
 * @date 2024/7/7
 */
public class CarNumDataMaskStrategy extends AbstractDataMaskStrategy {

    @Override
    public String mask(String data, IMaskContext context) throws DataMaskException {
        // 从第二位开始，保留最后一位
        return rangeMask(data, 2, 1);
    }

    @Override
    public String getType() {
        return "CAR_NUM";
    }
}
