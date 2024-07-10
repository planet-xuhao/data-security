package com.rhf.common.security.mask.strategy;

import com.rhf.common.security.mask.IMaskContext;
import com.rhf.common.security.mask.exception.DataMaskException;

/**
 * Description
 * 默认脱敏策略
 *
 * @author xuh
 * @date 2024/7/7
 */
public class DefaultDataStrategy extends AbstractDataMaskStrategy {

    @Override
    public String mask(String data, IMaskContext context) throws DataMaskException {
        if (data.length() == 2) {
            return rangeMask(data, 1, 0);
        } else {
            return rangeMask(data, 1, 1);
        }
    }

    @Override
    public String getType() {
        return "DEFAULT";
    }
}
