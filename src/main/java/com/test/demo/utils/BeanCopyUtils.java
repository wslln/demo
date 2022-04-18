package com.test.demo.utils;

import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author zhout 2022/3/16 2:30 下午
 */
@UtilityClass
public class BeanCopyUtils extends BeanUtils {

    @FunctionalInterface
    public interface BeanCopyUtilCallBack<S, T> {

        /**
         * 定义默认回调方法
         * 这里可以定义特定的转换规则
         * 因为属性不同的字段无法拷贝，如Integer sex,String sex。
         * 可利用回调方法实现自定义转换。
         */
        void callBack(S t, T s);
    }

    /**
     * 集合数据的拷贝
     *
     * @param sources: 数据源类
     * @param target:  目标类::new(eg: UserVO::new)
     */
    public static <S, T> List<T> copyListProperties(List<S> sources, Supplier<T> target) {
        return copyListProperties(sources, target, null);
    }

    /**
     * 带回调函数的集合数据的拷贝（可自定义字段拷贝规则）
     *
     * @param sources:  数据源类
     * @param target:   目标类::new(eg: UserDo::new)
     * @param callBack: 回调函数
     */
    public static <S, T> List<T> copyListProperties(
            List<S> sources, Supplier<T> target, BeanCopyUtilCallBack<S, T> callBack) {
        List<T> list = new ArrayList<>(sources.size());
        for (S source : sources) {
            T t = target.get();
            copyProperties(source, t);
            list.add(t);
            if (callBack != null) {
                // 回调
                callBack.callBack(source, t);
            }
        }
        return list;
    }
}
