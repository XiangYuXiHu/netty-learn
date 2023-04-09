package com.simle.registery;

import com.alibaba.nacos.api.exception.NacosException;

import java.net.InetSocketAddress;

/**
 * 服务发现接口
 *
 * @author smile
 */
public interface ServerDiscovery {

    /**
     * 根据服务名找到InetSocketAddress
     *
     * @param serviceName
     * @return
     * @throws NacosException
     */
    InetSocketAddress getService(String serviceName) throws NacosException;
}
