package com.simle.client;

import com.simle.id.SequenceIdGenerator;
import com.simle.message.RpcRequestMessage;
import io.netty.util.concurrent.DefaultPromise;

import java.lang.reflect.Proxy;

/**
 * @Description
 * @ClassName ClientProxy
 * @Author smile
 * @date 2022.03.29 14:59
 */
public class ClientProxy {

    private final RpcClientManager RPC_CLIENT;

    public ClientProxy(RpcClientManager clientManager) {
        this.RPC_CLIENT = clientManager;
    }

    public <T> T getProxyService(Class<T> serviceClass) {
        ClassLoader classLoader = serviceClass.getClassLoader();
        Class[] interfaces = new Class[]{serviceClass};
        Object o = Proxy.newProxyInstance(classLoader, interfaces, (proxy, method, args) -> {
            int sequenceId = SequenceIdGenerator.nextId();
            RpcRequestMessage rpcRequestMessage = new RpcRequestMessage(sequenceId,
                    serviceClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(), args);

            DefaultPromise<Object> promise = new DefaultPromise<>(RPC_CLIENT.nextEventLoop());
            RPC_CLIENT.addPromise(sequenceId, promise);
            RPC_CLIENT.sendRequest(rpcRequestMessage);
            promise.await();
            if (promise.isSuccess()) {
                return promise.getNow();
            } else {
                throw new RuntimeException(promise.cause());
            }
        });
        return (T) o;
    }

}
