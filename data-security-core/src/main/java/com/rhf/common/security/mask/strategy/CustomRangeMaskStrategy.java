package com.rhf.common.security.mask.strategy;

import com.rhf.common.security.mask.IMaskContext;
import com.rhf.common.security.mask.annotation.strategy.MaskRange;
import com.rhf.common.security.mask.exception.DataMaskException;

/**
 * Description
 * 自定义范围脱敏策略
 *
 * @author xuh
 * @date 2024/7/7
 */
public class CustomRangeMaskStrategy extends AbstractDataMaskStrategy {
    @Override
    public String mask(String data, IMaskContext context) throws DataMaskException {
        MaskRange range = context.getField().getAnnotation(MaskRange.class);
        return rangeMask(data, range.startLen(), range.afterLen());
    }

    @Override
    public String getType() {
        return "RANGE";
    }
}
