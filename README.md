## 项目结构

- data-security-annotation 存放脱敏相关的注解
- data-security-core 是核心的加密和脱敏实现包
- data-security-spring-boot-starter 是springboot2的实现支持，主要做core和spring-common模块的集成
- data-security-spring-common 提供了相关的spring参数和spring环境下的切面配置实现


## 安装

在springboot项目中可以快速的引入实现

```xml
<dependency>
    <groupId>com.fingard.rh.common.datasecurity</groupId>
    <artifactId>data-security-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```


## 数据加密

本包只完成了要对哪些数据进行加密或者解密的标注，具体的加解密实现由开发自行实现，或使用内置的AES加密。

### 加密注解

数据加密相关的注解有

- `@EncryptMethod` 用于注解在需要加密的方法上，添加该注解后才能被spring的aop拦截处理。 
- `@EncryptParam` 用于标注在方法参数上，标记入参需要做加密处理，支持String，不支持List<String>
- `@DecryptParam` 用于标注在方法参数上，标记入参需要做解密处理，支持String，不支持List<String>
- `@DecryptBody` 用于标注在加密方法上，表示返回的数据需要做解密
- `@EncryptBody` 用于标注在加密方法上，表示返回的数据需要做加密
- `@EncryptField` 标注在对象的字段上，如果作为入参的参数上标注了`@EncryptParam`或者`@DecryptParam`参数时会进行加密或者解密；作为返回值的时候会根据`@DecryptBody`和`@EncryptBody`做解密

### 最佳实践

我们加密的最初需求是对数据库的数据进行加密，最开始做的时候采用的是直接写mybatis拦截器实现的，但是我们还存在历史项目使用了hibernate，mybatis-plus等，
没有办法较好的覆盖所有场景。hibernate不能加注解，还要额外写拦截器，mybatis-plus也一样，而且拦截器还要考虑其他拦截器的影响，整体复杂度会高很多。
所以以下是推荐的加密的最佳实践：即对需要做加密的数据库层抽出一层manager层，所有的注解打在manager层上，在这个manager层上做加密就屏蔽了底层数据库层面的影响。
后续的样例都是基于这个方式来写的。


### 入参加密

#### 字符串入参加密

```java
@Component
public class TestManager{
    
    @Autowired
    private TestMapper mapper;
    
    @EncryptMethod
    public int countByName(@EncryptParam String name) {
        return mapper.countByName(name); 
    }
}

```

以上的代码就是data-security加密部分的最基础的用法了，对一个入参进行加密，在实际执行过程中`TestManager`会被代理，在调用countByName之前会先调用
data-security的加密切面，对入参进行加密处理。 

#### 对象入参加密

对象字段加密需要先在对象的指定字段上添加注解，如下处理：

```java
@Getter
@Setter
public class TestObj {
    public String normal;

    @EncryptField
    public String name;

}
```

对应的manager包装如下：

```java
@Component
public class TestManager{
    
    @Autowired
    private TestMapper mapper;
    
    @EncryptMethod
    public int countByName(@EncryptParam TestObj obj) {
        return mapper.countByName(obj.getName); 
    }
}
```

可以看到对象加密和字符串加密的方式是基本一致的，区别上就是对象需要额外在对象内添加注解。

### 返回值解密

我们沿用对象入参加密中的TestObj对象，对应的manager包装如下：

```java
@Component
public class TestManager{
    
    @Autowired
    private TestMapper mapper;
    
    @EncryptMethod
    @DecryptBody
    public List<TestObj> select() {
        return mapper.select(); 
    }
}
```

### 自定义密码学算子实现

data-security提供了除了保融加密算子以外的实现，目前只实现了AES，需要在application.yml中做如下配置：

```yaml
spring:
  data-security:
    encrypt:
      # 配置AES算子用于前后端数据加密交互
      operators:
        - type: com.fingard.rh.common.datasecurity.crypto.operator.AESOperator
          props:
            key: lWL37442r+4d4yvbtlmpCWZ+dYw95me1Y9IS8gmktrM=
```

- type 是指具体的算子实现类
- prop 是对应的算子实现类所需要的参数

如果要自己实现不同的加密方法，可以通过继承AbstractOperator，然后在配置文件中指定。在@EncryptMethod方法上可以指定具体的算子名称
`@EncryptMethod("AES")`,也可以在算子上标注primary表名是主算法，找不到算法的情况下会优先使用该算法，默认情况下的是AES算法。

```yaml
spring:
  data-security:
    encrypt:
      # 配置AES算子用于前后端数据加密交互
      operators:
        - type: com.fingard.rh.common.datasecurity.crypto.operator.AESOperator
          primary: true
          props:
            key: lWL37442r+4d4yvbtlmpCWZ+dYw95me1Y9IS8gmktrM=
```

## 数据脱敏

### 介绍

我们经常需要在页面显示时隐藏用户敏感信息，如身份证、手机号等。除了在数据库层面进行加密，还有在显示层上要做脱敏处理。data-security可以通过注解快速完成数据脱敏。

### 脱敏注解

- `@MaskChineseName` 对姓名进行脱敏
- `@MaskCardNo` 对卡号进行脱敏
- `@MaskPhone` 对手机号进行脱敏
- `@MaskIDCard` 对银行卡号进行脱敏
- `@MaskMethod` 用于标注在方法上说明返回值需要做脱敏处理
- `@MaskEncryptedMethod` 用于标注一个方法需要进行脱敏解密或者返回值加密处理，需要和加密功能一起使用

### 对象脱敏实例

两个步骤就可以完成脱敏：

1. 在对象上加对应的脱敏注解
2. 在需要做脱敏的方法或者接口上标注`@MaskMethod`

```java
@Getter
@Setter
public class TestObj {
    public String normal;

    @MashChineseName
    public String name;

}
```

```java
@Controller
public class TestManager{
    
    @Autowired
    private TestService testService;
    
    @MaskMethod
    public List<TestObj> select() {
        return testService.select(); 
    }
}
```

### 自定义脱敏注解

两个步骤：

1. 实现自己的脱敏策略，并注册
2. 采用复合注解，继承@MaskStrategy注解。

注册方式有两种，一种是通过bean的形式注入，还有一种是通过配置文件注入，这边仅介绍yaml配置文件。

**yaml配置**

yaml方式配置如下，共添加了两个策略。

```yaml
spring:
  data-security:
    mask:
      strategies:
        - com.fingard.rh.common.datasecurity.mask.strategy.IDCardDataMaskStrategy
        - com.fingard.rh.common.datasecurity.mask.strategy.PhoneDataMaskStrategy
```

**注解**

自定义的注解上面要添加复合注解，最主要的是`@MaskStrategy`里面要写清楚具体的策略名称，这个是在策略实现的时候固化在代码中的。

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MaskStrategy("PHONE")
public @interface MaskPhone {
}

```

### 条件注解

由于产品的一些需求，可能脱敏是存在条件的，所以data-security提供了三种条件注解方式。

#### 全局条件

data-security提供了`GlobalMaskCondition`接口， 在程序中实现该接口并通过导出为bean的方式，data-security会自动检测并注入。

具体的实现原理是： 在每一个方法调用之前，询问全局脱敏条件实现是否需要做脱敏，如果存在一个条件返回需要做脱敏则继续脱敏。


#### 脱敏条件注解

我们提供了@MaskCondition注解，该注解可以注解到类上和字段上，需要自己实现条件判断逻辑。


### 脱敏加密实现

脱敏就要彻底，不应该有我列表页面脱敏，编辑页面不脱敏，这种脱和不脱，只能靠权限来控制。

本工具提供了一种安全的编辑页脱敏的可行实现方案。

因为给前端的数据是脱敏的，那么传回给后端最大的问题就是，我怎么知道哪些数据变了，哪些数据没有变，会影响到最终需要将哪些数据落库。
我们换一种思路，如果将全部数据通过加密的形式返回给前端，前端无法解密，在编辑完成后将数据传递给后端，后端做解密不就可行了吗？

我们定义两个字段，一个字段叫做原始字段，该字段存储原始值，在进行数据脱敏的时候该字段会存储脱敏后的数据；另一个字段叫做加密字段，存储脱敏前的原始数据。
在返回给前端的时候先将原始数据采用一定的加密算法加密到指定加密字段，然后再对原始字段做脱敏返回给前端。这样前端拿到的数据原始字段是脱敏的，但是我们也存储
了一份加密的原始值数据给前端。

前端在处理的时候，如果加密的字段发生了变更，那么就直接将变更值放到加密字段上，在编辑完成后传回后端。 后端接受到编辑请求时，将加密字段上的数据进行解密覆盖到原始
字段上就能够得到原始的数据。

剩下一个问题是怎么知道加密字段上的数据加密了还是没有加密呢？这个比较容易解决，我们仅需要添加一些前缀符号区分就好。如果有前缀符号说明是加密数据直接解密，
如果没有前缀符号就不做处理。

在data-security中可以通过注解快速实现（但是加密字段需要自己加）,下面是对象的处理

```java
@Getter
@Setter
public class TestObj {
    public String normal;

    @MashChineseName
    @EncryptField(targetField = "nameEncrypted")
    public String name;
}
```

下面是方法上的处理。使用`@MaskEncryptMethod`标注方法需要进行脱敏和加解密处理。标注了`@EncryptBody`的方法将会对返回值进行额外的加密，这个会在脱敏之前完成。
标注了`@DecryptParam`的参数在实际入参之前会进行参数的解密，然后覆盖到原始字段上。

```java
@Controller
public class TestManager{
    
    @Autowired
    private TestService testService;
    
    @MaskEncryptMethod
    @EncryptBody
    public TestObj get() {
        return testService.get(); 
    }

    @MaskEncryptMethod
    public boolean save(@DecryptParam TestObj obj) {
        return testService.save(obj);
    }
}
```

**自定义算法**

data-security也提供了自定义算法替换，可能为了更安全的考虑，在前端编辑的时候需要使用rsa公钥对数据进行加密，选用RSA算法就是一种可能。

在data-security自定义脱敏的加密算法需要基于加密配置先提供一种自定义实现的算法，然后在脱敏功能中指定。配置如下：

```yaml
spring:
  data-security:
    encrypt:
      # 配置AES算子用于前后端数据加密交互
      operators:
        - type: com.fingard.rh.common.datasecurity.crypto.operator.AESOperator
          props:
            key: lWL37442r+4d4yvbtlmpCWZ+dYw95me1Y9IS8gmktrM=
    mask:
      encrypt-operator-name: AES
```

这个配置添加了一个自定义的AES算子，然后通过`encrypt-operator-name`指定脱敏加密使用AES算子。


## 完整配置

```yaml
# 用于开启或关闭整个组件的使用
spring.data-security.enabled=true
# 用于开启或关闭数据加密功能，不配置默认为true
spring.data-security.encrypt.enabled=true
# 用于配置加密AOP的执行顺序,默认要比脱敏的优先级低
spring.data-security.encrypt.aop-order=1

# 配置加密算法
spring.data-security.encrypt.operators[0].type=com.rhf.common.security.crypto.operator.AesOperator
# AES算子独立的属性配置
spring.data-security.encrypt.operators[0].props.key=lWL37442r+4d4yvbtlmpCWZ+dYw95me1Y9IS8gmktrM=
# 默认加密算法
spring.data-security.encrypt.operators[0].primary=true


# 配置保融加密算子，用的是核心开发的加密模块
spring.data-security.encrypt.operators[1].type=com.rhf.common.security.crypto.operator.ExampleOperator
# 自定义加密算法独特的属性配置,填绝对路径
spring.data-security.encrypt.operators[1].props.config-path=securityKey.properties

# 是否开启脱敏组件
spring.data-security.mask.enabled=true
# 配置AES算子用于前后端数据加密交互
spring.data-security.mask.encrypt-operator-name=AES
# 用于配置脱敏AOP的执行顺序，如果配置了这个，那么加密的aop顺序值必须要比这个大，默认不配置就是最高优先级
spring.data-security.mask.aop-order=-100
# 配置自定义的脱敏策略
spring.data-security.mask.strategies[0]=com.rhf.common.security.mask.strategy.CarNumDataMaskStrategy
```
