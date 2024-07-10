package com.rhf.common.security.mask.strategy;

import com.rhf.common.security.mask.IDataMaskStrategy;
import lombok.Setter;

/**
 * 脱敏策略基类
 *
 * @author xuh
 * @date 2024/7/7
 */
public abstract class AbstractDataMaskStrategy implements IDataMaskStrategy {

    private static final String DEFAULT_REPLACE_CHAR = "*";

    @Setter
    protected String replaceChar = DEFAULT_REPLACE_CHAR;

    /**
     * 任何脱敏类型都是将某一段内容替换为模糊字符
     *
     * @param value    待脱敏数据
     * @param startLen 前部分明文长度
     * @param afterLen 后部分明文长度
     * @return 脱敏后文本
     */
    protected String rangeMask(String value, int startLen, int afterLen) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        if (startLen < 0 || afterLen < 0) {
            throw new IllegalArgumentException("start len or after len can't less than zero");
        }
        // 无需脱敏
        if (startLen + afterLen >= value.length()) {
            return value;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < startLen; i++) {
            result.append(value.charAt(i));
        }
        int endLen = value.length() - afterLen;

        // 拼接上模糊字符
        result.append(String.valueOf(replaceChar).repeat(Math.max(0, endLen - startLen)));

        for (int i = endLen; i < value.length(); i++) {
            result.append(value.charAt(i));
        }
        return result.toString();
    }

}
