package com.simle.registery;

import com.alibaba.nacos.api.exception.NacosException;

import java.net.InetSocketAddress;

/**
 * 服务注册
 *
 * @author 12780
 */
public interface ServerRegistry {

    /**
     * 将服务名称与地址注册到服务注册中心
     *
     * @param serviceName
     * @param inetSocketAddress
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress) throws NacosException;
}
