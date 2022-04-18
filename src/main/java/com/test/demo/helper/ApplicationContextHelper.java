package com.test.demo.helper;

import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Locale;

/**
 * @author zhout 2022/3/4 6:01 下午
 */
@Component
public class ApplicationContextHelper {

    @Resource
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        ApplicationContextHelper.staticApplicationContext = applicationContext;
    }

    /**
     * 应用上下文
     */
    private static ApplicationContext staticApplicationContext;

    /**
     * 获取对象
     */
    public static <T> T getBean(Class<T> clazz) {
        return staticApplicationContext.getBean(clazz);
    }

    /**
     * 获取配置
     */
    public static String getProperty(String key) {
        return staticApplicationContext.getEnvironment().getProperty(key);
    }

    /**
     * 获取语言环境，默认从Accept-Language中获取 {@link org.springframework.web.filter.RequestContextFilter} {@link LocaleContextHolder}
     */
    public static Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }
}
