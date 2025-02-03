package com.nb6868.onex.common.validator;

import cn.hutool.core.util.ReflectUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Method;

/**
 * EnumValue校验方法
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class EnumValueValidator implements ConstraintValidator<EnumValue, Object> {

    private String[] strValues;
    private int[] intValues;
    private Class<? extends Enum<?>>[] enumClass;
    private String enumValidMethod;
    private boolean enumAllowNull;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        enumClass = constraintAnnotation.enumClass();
        enumValidMethod = constraintAnnotation.enumValidMethod();
        enumAllowNull = constraintAnnotation.enumAllowNull();
        strValues = constraintAnnotation.strValues();
        intValues = constraintAnnotation.intValues();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (enumClass != null && enumClass.length > 0) {
            // 校验是否有效的枚举值
            if (value == null) {
                return enumAllowNull;
            }
            Method method = ReflectUtil.getMethod(enumClass[0], enumValidMethod, value.getClass());
            return ReflectUtil.invokeStatic(method, value);
        } else {
            if (value instanceof String) {
                for (String s : strValues) {
                    if (s.equals(value)) {
                        return true;
                    }
                }
            } else if (value instanceof Integer) {
                for (int s : intValues) {
                    if (s == (Integer) value) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
