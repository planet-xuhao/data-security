package com.rhf.common.security.mask.fixture;

import com.rhf.common.security.mask.annotation.strategy.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @author xuh
 * @date 2024/7/10
 */
@Getter
@Setter
public class MaskObj {
    @MaskChineseName
    private String name;

    @MaskIDCard
    private String idCard;

    @MaskPhone
    private String phone;

    @MaskCardNo
    private String bankCard;

    @MaskStrategy
    private String defaultText;

    @MaskRange(startLen = 1,afterLen = 1)
    private String range;

    private Integer wrapperData;
}
