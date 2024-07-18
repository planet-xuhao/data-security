package com.rhf.common.security.fixture;

import com.rhf.common.security.fixture.controller.DataMaskController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author xuh
 * @date 2024/7/19
 */
@SpringBootTest(classes = {DataSecuritySpringBootTest.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class MaskTest {


    private String expectDefaultTextEncrypted = "$AES_WWo75QyNFFEFdqSYVqL/aA==";

    @Autowired
    private DataMaskController dataMaskController;

    public DataMaskController.MaskObj getTemplate() {
        DataMaskController.MaskObj obj = new DataMaskController.MaskObj();
        obj.setName("张三");
        obj.setPhone("15885100889");
        obj.setIdCard("5225421995081209832");
        obj.setBankCard("005423102411562094106315");
        obj.setEmail("test11@fingard.com");
        obj.setCarNum("浙AQF673");
        obj.setDefaultText("defaultxxx");
        obj.setWrapperData(1);
        return obj;
    }


    public void isSameWithTemplate(DataMaskController.MaskObj maskObj) {
        DataMaskController.MaskObj template = getTemplate();
        Assertions.assertEquals(template.getName(), maskObj.getName());
        Assertions.assertEquals(template.getIdCard(), maskObj.getIdCard());
        Assertions.assertEquals(template.getPhone(), maskObj.getPhone());
        Assertions.assertEquals(template.getBankCard(), maskObj.getBankCard());
        Assertions.assertEquals(template.getEmail(), maskObj.getEmail());
        Assertions.assertEquals(template.getCarNum(), maskObj.getCarNum());
        Assertions.assertEquals(template.getDefaultText(), maskObj.getDefaultText());
        Assertions.assertEquals(1, maskObj.getWrapperData());
    }

    public void compareResult(DataMaskController.MaskObj obj) {
        Assertions.assertEquals("张*", obj.getName());
        Assertions.assertEquals("522542***********32", obj.getIdCard());
        Assertions.assertEquals("158****0889", obj.getPhone());
        Assertions.assertEquals("0*******************6315", obj.getBankCard());
        Assertions.assertEquals("d********x", obj.getDefaultText());
        Assertions.assertEquals("t****1@fingard.com", obj.getEmail());
        Assertions.assertEquals("浙A****3", obj.getCarNum());
        Assertions.assertEquals(1, obj.getWrapperData());
    }

    @Test
    void testMaskObj() {
        DataMaskController.MaskObj obj = this.getTemplate();
        DataMaskController.MaskObj result = dataMaskController.maskObject(obj);
        // 校验
        compareResult(result);
    }

    @Test
    @DisplayName("测试返回结果脱敏且加密")
    void testMaskAndEncrypt() {
        DataMaskController.MaskObj maskObj = getTemplate();
        dataMaskController.maskAndEncrypt(maskObj);
        compareResult(maskObj);
        Assertions.assertEquals(expectDefaultTextEncrypted, maskObj.getDefaultTextEncrypted());
    }

    @Test
    @DisplayName("测试参数解密且覆盖脱敏字段")
    void testDecryptMaskParam() {
        DataMaskController.MaskObj maskObj = getTemplate();
        maskObj.setDefaultTextEncrypted(expectDefaultTextEncrypted);
        // 将默认字段置空，这个预期应该会对其进行解密
        maskObj.setDefaultText(null);
        dataMaskController.decryptMaskParam(maskObj, getTemplate());
    }


    @Test
    @DisplayName("泛型的脱敏测试")
    void testGenericMask() {
        DataMaskController.GenericObj<DataMaskController.MaskObj> obj = new DataMaskController.GenericObj<>();
        DataMaskController.MaskObj maskObj = getTemplate();
        obj.setData(maskObj);
        dataMaskController.maskGeneric(obj);
        compareResult(maskObj);
    }

    @Test
    @Disabled
    void performanceTest() {
        DataMaskController.MaskObj maskObj = getTemplate();
        for (int i = 0; i < 1000000; i++) {
            dataMaskController.simpleMaskObject(maskObj);
        }
    }
}
