package com.rhf.common.security.fixture.service;

import com.rhf.common.security.crypto.annotation.EncryptField;
import lombok.Getter;
import lombok.Setter;

/**
 * @author xuh
 * @date 2024/7/18
 */
@Getter
@Setter
public class EncryptServiceObj {

    private String bankCard;

    @EncryptField(targetField = "defaultTextEncrypted")
    private String defaultText;

    private String defaultTextEncrypted;
}
