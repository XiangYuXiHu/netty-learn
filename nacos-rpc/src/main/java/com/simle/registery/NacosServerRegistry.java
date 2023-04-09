package com.simle.registery;

import com.alibaba.nacos.api.exception.NacosException;

import java.net.InetSocketAddress;

/**
 * nacos注册
 *
 * @Description
 * @ClassName NacosServerRegistry
 * @Author smile
 * @date 2022.08.27 14:55
 */
public class NacosServerRegistry implements ServerRegistry {
    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) throws NacosException {
        NacosUtils.registerServer(serviceName, inetSocketAddress);
    }
}
