package com.simle;

import com.simle.anno.RpcServerScan;
import com.simle.client.RpcServiceManager;

/**
 * @Description
 * @ClassName RpcServer
 * @Author smile
 * @date 2022.04.01 19:19
 */
@RpcServerScan
public class RpcServer {

    public static void main(String[] args) {
        new RpcServiceManager("192.168.1.102", 8080).start();
    }
}
