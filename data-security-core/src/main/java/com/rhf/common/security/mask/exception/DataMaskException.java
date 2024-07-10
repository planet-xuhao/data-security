package com.rhf.common.security.mask.exception;

import com.rhf.common.security.crypto.exception.CryptoException;

/**
 * @author xuh
 * @date 2024/7/7
 */
public class DataMaskException extends CryptoException {

    public DataMaskException() {
        super();
    }

    public DataMaskException(String message) {
        super(message);
    }

    public DataMaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataMaskException(Throwable cause) {
        super(cause);
    }

    public DataMaskException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
