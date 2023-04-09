package com.simle.registery;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Description
 * @ClassName NacosUtils
 * @Author smile
 * @date 2022.08.27 14:56
 */
@Slf4j
public class NacosUtils {

    private static final NamingService namingService;

    private static final Set<String> serviceNames = new HashSet<>();

    private static InetSocketAddress address;

    private static final String SERVER_ADDR = "192.168.21.128:8848";

    static {
        namingService = getNacosNamingService();
    }

    /**
     * 初始化
     *
     * @return
     */
    public static NamingService getNacosNamingService() {
        try {
            return NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            log.error("连接nacos异常:{}", e.getMessage());
            throw new RuntimeException("连接Nacos异常");
        }
    }

    /**
     * 注册服务
     *
     * @param serverName
     * @param address
     */
    public static void registerServer(String serverName, InetSocketAddress address) throws NacosException {
        namingService.registerInstance(serverName, address.getHostName(), address.getPort());
        NacosUtils.address = address;
        serviceNames.add(serverName);
    }

    /**
     * 获取服务名中所有实例
     *
     * @param serverName
     * @return
     * @throws NacosException
     */
    public static List<Instance> getAllInstance(String serverName) throws NacosException {
        return namingService.getAllInstances(serverName);
    }

    /**
     * 注销服务
     *
     * @throws NacosException
     */
    public static void clearRegister() throws NacosException {
        if (!serviceNames.isEmpty() && null != address) {
            int port = address.getPort();
            String hostName = address.getHostName();
            for (String serviceName : serviceNames) {
                namingService.deregisterInstance(serviceName, hostName, port);
            }
        }
    }
}
