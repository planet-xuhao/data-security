package com.rhf.common.security.mask;

import com.rhf.common.security.mask.fixture.ConditionObj;
import com.rhf.common.security.mask.fixture.FieldConditionObj;
import com.rhf.common.security.mask.fixture.MaskObj;
import com.rhf.common.security.mask.fixture.RootObj;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 脱敏功能单元测试
 *
 * @author xuh
 * @date 2024/7/10
 */
public class DefaultDataMaskSpecTest {

    private static DefaultDataMaskSpec dataMaskSpec;

    @BeforeAll
    static void setup() {
        MaskStrategyManager maskStrategyManager = new MaskStrategyManager();
        dataMaskSpec = new DefaultDataMaskSpec(maskStrategyManager);
    }

    public void initTemplate(MaskObj obj) {
        obj.setName("张三");
        obj.setPhone("15885100889");
        obj.setIdCard("5225421995081209832");
        obj.setBankCard("005423102411562094106315");
        obj.setDefaultText("defaultxxx");
        obj.setWrapperData(1);
        obj.setRange("11111");
    }

    public void compareResult(MaskObj obj) {
        assertEquals("张*", obj.getName());
        assertEquals("522542***********32", obj.getIdCard());
        assertEquals("158****0889", obj.getPhone());
        assertEquals("0*******************6315", obj.getBankCard());
        assertEquals("d********x", obj.getDefaultText());
        assertEquals(1, obj.getWrapperData());
        assertEquals("1***1", obj.getRange());
    }

    public void compareResultObj(MaskObj obj) {
        assertEquals("李*", obj.getName());
        assertEquals("522542***********32", obj.getIdCard());
        assertEquals("158****0889", obj.getPhone());
        assertEquals("0*******************6315", obj.getBankCard());
        assertEquals("d********x", obj.getDefaultText());
        assertEquals(1, obj.getWrapperData());
        assertEquals("1***1", obj.getRange());
    }

    public boolean isSameWithTemplate(MaskObj obj) {
        MaskObj templateObj = new MaskObj();
        initTemplate(templateObj);
        return Objects.equals(templateObj.getName(), obj.getName())
                && Objects.equals(templateObj.getIdCard(), obj.getIdCard())
                && Objects.equals(templateObj.getPhone(), obj.getPhone())
                && Objects.equals(templateObj.getBankCard(), obj.getBankCard())
                && Objects.equals(templateObj.getDefaultText(), obj.getDefaultText())
                && Objects.equals(1, obj.getWrapperData())
                && Objects.equals(templateObj.getRange(), obj.getRange());
    }

    @Test
    @DisplayName("普通对象脱敏")
    void testMaskObj() {
        // 数据准备
        MaskObj obj = new MaskObj();
        this.initTemplate(obj);
        // 脱敏
        dataMaskSpec.mask(obj);
        // 校验
        compareResult(obj);
    }

    @Test
    @DisplayName("集合脱敏")
    void testMaskCollection() {
        int len = 2;
        List<MaskObj> list = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            MaskObj obj = new MaskObj();
            initTemplate(obj);
            list.add(obj);
        }
        dataMaskSpec.mask(list);
        for (int i = 0; i < len; i++) {
            compareResult(list.get(i));
        }
    }

    @Test
    @DisplayName("测试内嵌对象脱敏")
    void testNestedMask() {
        MaskObj maskObj = new MaskObj();
        initTemplate(maskObj);
        RootObj rootObj = new RootObj();
        rootObj.setMaskObj(maskObj);
        dataMaskSpec.mask(rootObj);
        compareResult(rootObj.getMaskObj());
    }

    @Test
    @DisplayName("条件脱敏")
    void testConditionMask() {
        ConditionObj conditionObj1 = new ConditionObj();
        initTemplate(conditionObj1);
        dataMaskSpec.mask(conditionObj1);
        // 预期整个对象都和模版对象一致
        assertTrue(isSameWithTemplate(conditionObj1));

        ConditionObj conditionObj2 = new ConditionObj();
        initTemplate(conditionObj2);
        conditionObj2.setName("李四");
        dataMaskSpec.mask(conditionObj2);
        // 预期整个对象都和模版对象一致
        compareResultObj(conditionObj2);


        // 字段条件不脱敏
        FieldConditionObj obj1 = new FieldConditionObj();
        obj1.setName("张三");
        dataMaskSpec.mask(obj1);
        assertEquals("张三", obj1.getName());

        // 字段条件脱敏
        FieldConditionObj obj2 = new FieldConditionObj();
        obj2.setName("李四");
        dataMaskSpec.mask(obj2);
        assertEquals("李*", obj2.getName());
    }
}
