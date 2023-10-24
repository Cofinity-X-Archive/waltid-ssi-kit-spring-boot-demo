package com.cofinityx.waltid.waltidssikitdemo.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


@Component
public class StaticContextAccessor  {

    private static StaticContextAccessor instance;

    private final ApplicationContext applicationContext;


    public StaticContextAccessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        instance = this;
    }


    public static <T> T getBean(Class<T> clazz) {
        return instance.applicationContext.getBean(clazz);
    }


}
