package com.rhf.common.security.fixture.service;

import com.rhf.common.security.crypto.annotation.EncryptMethod;
import com.rhf.common.security.crypto.annotation.EncryptParam;
import org.springframework.stereotype.Service;

/**
 * @author xuh
 * @date 2024/7/18
 */
@Service
public class EncryptServiceImpl implements EncryptService {

    @EncryptMethod
    public String getObj(@EncryptParam String param) {
        return param;
    }
}
