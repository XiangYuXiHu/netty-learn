package com.simle.client;

import com.alibaba.nacos.api.exception.NacosException;
import com.simle.handler.HeartbeatClientHandler;
import com.simle.handler.RpcResponseMessageHandler;
import com.simle.loadBalance.RoundRobinRule;
import com.simle.message.RpcRequestMessage;
import com.simle.protocol.MessageCodecSharable;
import com.simle.protocol.ProtocolFrameDecoder;
import com.simle.registery.NacosServerDiscovery;
import com.simle.registery.ServerDiscovery;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description
 * @ClassName RpcClientManager
 * @Author smile
 * @date 2022.03.29 15:00
 */
@Slf4j
public class RpcClientManager {

    private static final Bootstrap bootstrap;
    private static final NioEventLoopGroup group;
    private final ServerDiscovery serverDiscovery;
    private static final Map<Integer, Promise<Object>> PROMISES;
    private static Map<String, Channel> channels = new ConcurrentHashMap<>();

    static {
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        initChannel();
        PROMISES = new ConcurrentHashMap<Integer, Promise<Object>>();
    }

    public RpcClientManager() {
        serverDiscovery = new NacosServerDiscovery(new RoundRobinRule());
    }

    private static void initChannel() {
        LoggingHandler LOGGER_HANDLER = new LoggingHandler(LogLevel.INFO);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();
        HeartbeatClientHandler HEARTBEAT_CLIENT = new HeartbeatClientHandler();

        bootstrap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(0, 15, 0));
                        ch.pipeline().addLast(LOGGER_HANDLER);
                        ch.pipeline().addLast(new ProtocolFrameDecoder());
                        ch.pipeline().addLast(MESSAGE_CODEC);
                        ch.pipeline().addLast(HEARTBEAT_CLIENT);
                        ch.pipeline().addLast(RPC_HANDLER);
                    }
                });
    }

    public static Channel get(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        if (channels.containsKey(key)) {
            Channel channel = channels.get(key);
            if (null != channel && channel.isActive()) {
                return channel;
            }
            channels.remove(channel);
        }
        Channel channel = null;
        try {
            channel = bootstrap.connect(inetSocketAddress).sync().channel();
            channel.closeFuture().addListener(future -> {
                log.info("连接断开:{}", future.get());
            });
        } catch (InterruptedException e) {
            channel.close();
            log.error("客户端连接异常:{}", e.getMessage());
            return null;
        }
        channels.put(key, channel);
        return channel;
    }

    public void sendRequest(RpcRequestMessage msg) throws NacosException {
        InetSocketAddress inetSocketAddress = serverDiscovery.getService(msg.getInterfaceName());
        Channel channel = get(inetSocketAddress);
        if (null != channel) {
            if (!channel.isActive() || !channel.isRegistered()) {
                group.shutdownGracefully();
                return;
            }
            channel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("客户端发送消息成功");
                }
            });
        }
    }

    /**
     * 添加promise
     *
     * @param sequenceId
     * @param promise
     */
    public void addPromise(Integer sequenceId, Promise<Object> promise) {
        PROMISES.put(sequenceId, promise);
    }

    /**
     * 获取eventLoop
     *
     * @return
     */
    public EventLoop nextEventLoop() {
        return group.next();
    }
}
