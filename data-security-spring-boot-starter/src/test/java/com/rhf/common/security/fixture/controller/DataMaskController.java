package com.rhf.common.security.fixture.controller;

import com.rhf.common.security.crypto.annotation.*;
import com.rhf.common.security.mask.annotation.strategy.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xuh
 * @date 2024/7/19
 */
@Service
public class DataMaskController {

    @Getter
    @Setter
    public static class MaskObj {
        @MaskChineseName
        private String name;

        @MaskIDCard
        private String idCard;

        @MaskPhone
        private String phone;

        @MaskCardNo
        private String bankCard;

        @MaskEmail
        private String email;

        /**
         * 车牌号
         */
        @MaskCarNum
        private String carNum;

        @MaskStrategy
        @EncryptField(targetField = "defaultTextEncrypted")
        private String defaultText;

        private String defaultTextEncrypted;

        private Integer wrapperData;
    }

    @MaskMethod
    public MaskObj simpleMaskObject(MaskObj maskObj) {
        return maskObj;
    }

    @EncryptMethod
    @MaskMethod
    public MaskObj maskObject(@EncryptParam MaskObj templateObj) {
        MaskObj obj = new MaskObj();
        BeanUtils.copyProperties(templateObj, obj);
        return obj;
    }

    @MaskMethod
    public List<MaskObj> maskCollection(int len, MaskObj templateObj) {
        List<MaskObj> list = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            MaskObj obj = new MaskObj();
            BeanUtils.copyProperties(templateObj, obj);
            list.add(obj);
        }
        return list;
    }

    @MaskMethod
    @EncryptBody
    @MaskEncryptedMethod
    public MaskObj maskAndEncrypt(MaskObj maskObj) {
        return maskObj;
    }

    @MaskEncryptedMethod
    public void decryptMaskParam(@DecryptParam MaskObj maskObj, MaskObj expectObj) {
        // 需要进行解密参数的比对
        if (!maskObj.getDefaultText().equals(expectObj.getDefaultText())) {
            throw new RuntimeException("验证失败");
        }
    }


    @Getter
    @Setter
    public static class GenericObj<T> {
        T data;
    }

    @MaskMethod
    public GenericObj<?> maskGeneric(GenericObj<?> obj) {
        return obj;
    }
}
