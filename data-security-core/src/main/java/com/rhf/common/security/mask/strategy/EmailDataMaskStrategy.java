package com.rhf.common.security.mask.strategy;

import com.rhf.common.security.mask.IMaskContext;
import com.rhf.common.security.mask.exception.DataMaskException;

/**
 * Description
 * 邮箱脱敏策略
 * 对域名之前的信息脱敏第一位和最后一位
 *
 * @author xuh
 * @date 2024/7/7
 */
public class EmailDataMaskStrategy extends AbstractDataMaskStrategy {
    @Override
    public String mask(String data, IMaskContext context) throws DataMaskException {
        // 获取@之前需要脱敏，之后的域名部分不需要脱敏
        int userIndex = data.indexOf('@');
        if (userIndex < 0) {
            return data;
        }
        String username = data.substring(0, userIndex);
        String domain = data.substring(userIndex);
        String maskUsername = rangeMask(username, 1, 1);
        return maskUsername + domain;
    }

    @Override
    public String getType() {
        return "EMAIL";
    }
}
