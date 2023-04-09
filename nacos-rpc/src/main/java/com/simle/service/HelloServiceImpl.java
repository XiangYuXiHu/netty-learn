package com.simle.service;

import com.simle.anno.RpcServer;

/**
 * @Description
 * @ClassName HelloServiceImpl
 * @Author smile
 * @date 2022.04.01 17:53
 */
@RpcServer
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "hai " + name;
    }
}
