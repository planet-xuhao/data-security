package com.rhf.common.security.fixture;

import com.rhf.common.security.fixture.manager.DataEncryptManager;
import com.rhf.common.security.fixture.service.EncryptService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author xuh
 * @date 2024/7/18
 */
@SpringBootTest(classes = {DataSecuritySpringBootTest.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class EncryptTest {

    private final String planText = "张三";
    private final String encryptedText = "$AES_lTXqEZHxIUJfLW30JBkbow==";

    @Autowired
    private DataEncryptManager manager;

    @Autowired
    private EncryptService encryptService;

    @Test
    @DisplayName("单参数加密")
    void test1() {
        String result = manager.encryptParam(planText);
        Assertions.assertEquals(encryptedText, result);
    }

    @Test
    @DisplayName("多参数加密")
    void test2() {
        List<String> result = manager.encryptMultipleParam(planText, planText);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(encryptedText, result.get(0));
        Assertions.assertEquals(encryptedText, result.get(1));
    }

    @Test
    @DisplayName("对象参数加密")
    void testObjectParameter() {
        DataEncryptManager.EncryptObject obj = new DataEncryptManager.EncryptObject();
        obj.setName(planText);
        DataEncryptManager.EncryptObject result = manager.encryptObjectParameter(obj);
        Assertions.assertEquals(encryptedText, result.getName());
    }

    @Test
    @DisplayName("内嵌对象参数加密")
    void testNestedObjectParameter() {
        DataEncryptManager.TopObject obj = new DataEncryptManager.TopObject();
        DataEncryptManager.NestedObject nestedObject = new DataEncryptManager.NestedObject();
        nestedObject.setName(planText);
        obj.setNestedObject(nestedObject);
        DataEncryptManager.TopObject result = manager.nestedObjectParameter(obj);
        // 校验
        Assertions.assertEquals(planText, obj.getNestedObject().getName());
        Assertions.assertEquals(encryptedText, result.getNestedObject().getName());
    }

    @Test
    @DisplayName("基本类型加密-预期不支持")
    void testPrimitiveParameter() {
        int result = manager.nestedObjectParameter(3);
        Assertions.assertEquals(3, result);
    }

    @Test
    @DisplayName("集合参数加密")
    void testCollectionParameter() {
        List<DataEncryptManager.EncryptObject> objs = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            DataEncryptManager.EncryptObject obj = new DataEncryptManager.EncryptObject();
            obj.setName(planText);
            objs.add(obj);
        }
        List<DataEncryptManager.EncryptObject> result = manager.collectionParameter(objs);

        // 验证
        Assertions.assertEquals(3, result.size());
        // 参数应该被还原
        for (DataEncryptManager.EncryptObject encryptObject : objs) {
            Assertions.assertEquals(planText, encryptObject.getName());
        }
        for (DataEncryptManager.EncryptObject obj : result) {
            Assertions.assertEquals(encryptedText, obj.getName());
        }
    }

    @Test
    @DisplayName("Map参数加密")
    void testMapParameter() {
        Map<String, DataEncryptManager.EncryptObject> map = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            DataEncryptManager.EncryptObject obj = new DataEncryptManager.EncryptObject();
            obj.setName(planText);
            map.put(String.valueOf(i), obj);
        }
        Map<String, DataEncryptManager.EncryptObject> result = manager.mapParameter(map);

        // 验证
        Assertions.assertEquals(3, result.size());
        // 参数应该被还原
        for (Map.Entry<String, DataEncryptManager.EncryptObject> entry : map.entrySet()) {
            Assertions.assertEquals(planText, entry.getValue().getName());
        }
        for (Map.Entry<String, DataEncryptManager.EncryptObject> entry : result.entrySet()) {
            Assertions.assertEquals(encryptedText, entry.getValue().getName());
        }
    }

    @Test
    @DisplayName("返回字符串解密")
    void test3() {
        String result = manager.returnString(encryptedText);
        Assertions.assertEquals(planText, result);
    }

    @Test
    @DisplayName("返回对象解密")
    void testReturnObject() {
        DataEncryptManager.EncryptObject obj = manager.returnObject(encryptedText);
        Assertions.assertEquals(planText, obj.getName());
    }

    @Test
    @DisplayName("返回集合解密")
    void testReturnCollection() {
        List<DataEncryptManager.EncryptObject> result = manager.returnCollection(3, encryptedText);
        // 验证
        Assertions.assertEquals(3, result.size());
        for (DataEncryptManager.EncryptObject obj : result) {
            // 应该被还原
            Assertions.assertEquals(planText, obj.getName());
        }
    }

    @Test
    @DisplayName("测试接口实现")
    void testInterfaceImpl() {
        String result = encryptService.getObj(planText);
        Assertions.assertEquals(encryptedText, result);
    }

    @Test
    @DisplayName("对返回List<String>做数据解密")
    void testDecryptList() {
        List<String> result = manager.returnListString(3, encryptedText);
        Assertions.assertEquals(3, result.size());
        for (String s : result) {
            Assertions.assertEquals(planText, s);
        }
    }
}
