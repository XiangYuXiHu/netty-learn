package com.simle.factory;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description
 * @ClassName ServiceFactory
 * @Author smile
 * @date 2022.03.31 20:41
 */
@Slf4j
public class ServiceFactory {

    public static final Map<String, Object> serviceFactory = new ConcurrentHashMap<>();

    public static <T> void addServiceProvider(T service, String serviceName) {
        if (serviceFactory.containsKey(serviceName)) {
            return;
        }
        serviceFactory.put(serviceName, service);
    }

    public static Object getServiceProvider(String serviceName) {
        Object service = serviceFactory.get(serviceName);
        if (null == service) {
            throw new RuntimeException("未发现服务");
        }
        return service;
    }
}
