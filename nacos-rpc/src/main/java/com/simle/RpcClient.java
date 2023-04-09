package com.simle;

import com.simle.client.ClientProxy;
import com.simle.client.RpcClientManager;
import com.simle.service.HelloService;
import com.simle.service.HelloServiceImpl;

/**
 * @Description
 * @ClassName RpcClient
 * @Author smile
 * @date 2022.04.01 19:15
 */
public class RpcClient {
    public static void main(String[] args) {
        RpcClientManager clientManager = new RpcClientManager();
        //创建代理对象
        HelloService service = new ClientProxy(clientManager).getProxyService(HelloService.class);
        System.out.println(service.sayHello("baby"));
    }
}
