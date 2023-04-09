package com.simle.registery;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.simle.loadBalance.LoadBalancer;
import com.simle.loadBalance.RandomRule;
import com.simle.loadBalance.RoundRobinRule;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Description
 * @ClassName NacosServerDiscovery
 * @Author smile
 * @date 2022.08.27 16:19
 */
public class NacosServerDiscovery implements ServerDiscovery {
    private final LoadBalancer loadBalancer;

    public NacosServerDiscovery(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer == null ? new RoundRobinRule() : new RandomRule();
    }

    @Override
    public InetSocketAddress getService(String serviceName) throws NacosException {
        List<Instance> allInstance = NacosUtils.getAllInstance(serviceName);
        if (CollectionUtils.isEmpty(allInstance)) {
            throw new RuntimeException("服务列表为空");
        }
        Instance instance = loadBalancer.getInstance(allInstance);
        return new InetSocketAddress(instance.getIp(), instance.getPort());
    }
}
