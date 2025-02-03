package com.nb6868.onex.common.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 限定枚举值传参
 * see <a href="https://blog.csdn.net/Lieforlove/article/details/101370087">...</a>
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {EnumValueValidator.class})
@Repeatable(EnumValue.List.class)
public @interface EnumValue {

    // 默认错误消息
    String message() default "必须为指定值";

    String[] strValues() default {};

    int[] intValues() default {};

    Class<? extends Enum<?>>[] enumClass() default {}; // 枚举类

    String enumValidMethod() default "isValid"; // 枚举校验方法

    boolean enumAllowNull() default false; // 枚举是否允许为空

    // 分组
    Class<?>[] groups() default {};

    // 负载
    Class<? extends Payload>[] payload() default {};

    // 指定多个时使用
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        EnumValue[] value();
    }
}
