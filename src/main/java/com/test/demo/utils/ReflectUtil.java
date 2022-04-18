package com.test.demo.utils;

import com.google.common.base.Strings;
import lombok.experimental.UtilityClass;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author zhout 2021/12/28 5:20 下午
 */
@UtilityClass
public class ReflectUtil {

    /**
     * 通过字段名从对象或对象的父类中得到字段的值
     *
     * @param object    对象实例
     * @param fieldName 字段名
     * @return 字段对应的值
     */
    public static Object getValue(Object object, String fieldName) {
        if (Objects.isNull(object) || Strings.isNullOrEmpty(fieldName)) {
            return null;
        }
        Field field;
        Class<?> clazz = object.getClass();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(object);
            } catch (Exception e) {
                // 这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                // 如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了
            }
        }
        return null;
    }

    /**
     * 通过字段名从对象或对象的父类中得到字段的值（调用字典的get方法）
     *
     * @param object    对象实例
     * @param fieldName 字段名
     * @return 字段对应的值
     */
    public static Object getValueOfGet(Object object, String fieldName) {
        if (Objects.isNull(object) || Strings.isNullOrEmpty(fieldName)) {
            return null;
        }
        Field field;
        Class<?> clazz = object.getClass();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
                //获得get方法
                Method getMethod = pd.getReadMethod();
                //执行get方法返回一个Object
                return getMethod.invoke(object);
            } catch (Exception e) {
                //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了
            }
        }
        return null;
    }
}
