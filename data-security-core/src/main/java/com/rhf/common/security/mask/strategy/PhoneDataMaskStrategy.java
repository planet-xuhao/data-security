package com.rhf.common.security.mask.strategy;

import com.rhf.common.security.mask.IMaskContext;
import com.rhf.common.security.mask.exception.DataMaskException;

/**
 * Description
 * 手机号脱敏策略
 * 保留前3位和后4位
 *
 * @author xuh
 * @date 2024/7/7
 */
public class PhoneDataMaskStrategy extends AbstractDataMaskStrategy {

    @Override
    public String mask(String data, IMaskContext context) throws DataMaskException {
        if (data.length() <= 7) {
            return rangeMask(data, 1, 1);
        } else {
            return rangeMask(data, 3, 4);
        }
    }

    @Override
    public String getType() {
        return "PHONE";
    }
}
