package com.rhf.common.security.fixture.manager;

import com.rhf.common.security.crypto.annotation.DecryptBody;
import com.rhf.common.security.crypto.annotation.EncryptField;
import com.rhf.common.security.crypto.annotation.EncryptMethod;
import com.rhf.common.security.crypto.annotation.EncryptParam;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author xuh
 * @date 2024/7/18
 */
@Service
public class DataEncryptManager {

    /**
     * 单个对象
     */
    @Getter
    @Setter
    public static class EncryptObject {
        @EncryptField
        private String name;
    }

    /**
     * 嵌套对象
     */
    @Getter
    @Setter
    public static class TopObject {
        private NestedObject nestedObject;
    }

    @Getter
    @Setter
    public static class NestedObject {
        @EncryptField
        private String name;
    }

    /**
     * 对请求参数进行加密
     */
    @EncryptMethod
    public String encryptParam(@EncryptParam String name) {
        return name;
    }

    /**
     * 对请求参数进行加密
     */
    @EncryptMethod
    public List<String> encryptMultipleParam(@EncryptParam String name, @EncryptParam String phone) {
        List<String> list = new LinkedList<>();
        list.add(name);
        list.add(phone);
        return list;
    }

    /**
     * 对请求参数是对象进行加密
     */
    @EncryptMethod
    public EncryptObject encryptObjectParameter(@EncryptParam EncryptObject object) {
        EncryptObject obj = new EncryptObject();
        obj.setName(object.getName());
        return obj;
    }

    /**
     * 嵌套对象加密
     */
    @EncryptMethod
    public TopObject nestedObjectParameter(@EncryptParam TopObject object) {
        TopObject topObject = new TopObject();
        NestedObject nestedObject = new NestedObject();
        nestedObject.setName(object.getNestedObject().getName());
        topObject.setNestedObject(nestedObject);
        return topObject;
    }

    /**
     * 基本类型加密
     */
    @EncryptMethod
    public int nestedObjectParameter(@EncryptParam int param) {
        return param;
    }

    /**
     * 集合对象加密
     */
    @EncryptMethod
    public List<EncryptObject> collectionParameter(@EncryptParam List<EncryptObject> param) {
        List<EncryptObject> list = new LinkedList<>();
        for (EncryptObject obj : param) {
            EncryptObject clone = new EncryptObject();
            clone.setName(obj.getName());
            list.add(clone);
        }
        return list;
    }

    /**
     * map参数对象加密
     */
    @EncryptMethod
    public Map<String, EncryptObject> mapParameter(@EncryptParam Map<String, EncryptObject> param) {
        Map<String, EncryptObject> map = new HashMap<>();
        for (Map.Entry<String, EncryptObject> entry : param.entrySet()) {
            EncryptObject clone = new EncryptObject();
            clone.setName(entry.getValue().getName());
            map.put(entry.getKey(), clone);
        }
        return map;
    }

    /**
     * 对返回结果解密
     */
    @DecryptBody
    @EncryptMethod
    public String returnString(String name) {
        return name;
    }

    /**
     * 请求参数解密
     */
    @DecryptBody
    @EncryptMethod
    public EncryptObject returnObject(String text) {
        EncryptObject obj = new EncryptObject();
        obj.setName(text);
        return obj;
    }

    @DecryptBody
    @EncryptMethod
    public List<EncryptObject> returnCollection(int len, String text) {
        List<EncryptObject> list = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            EncryptObject obj = new EncryptObject();
            obj.setName(text);
            list.add(obj);
        }
        return list;
    }

    @DecryptBody
    @EncryptMethod
    public List<String> returnListString(int len, String text) {
        List<String> list = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            list.add(text);
        }
        return list;
    }
}
