package com.simle.client;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.common.utils.StringUtils;
import com.simle.anno.RpcServer;
import com.simle.anno.RpcServerScan;
import com.simle.factory.ServiceFactory;
import com.simle.handler.HeartbeatServerHandler;
import com.simle.handler.PingMessageHandler;
import com.simle.handler.RpcRequestMessageHandler;
import com.simle.protocol.MessageCodecSharable;
import com.simle.protocol.ProtocolFrameDecoder;
import com.simle.registery.NacosServerRegistry;
import com.simle.registery.ServerRegistry;
import com.simle.scan.PackageScanUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * @Description
 * @ClassName RpcServiceManager
 * @Author smile
 * @date 2022.08.27 14:51
 */
@Slf4j
public class RpcServiceManager {

    private String host;
    private int port;
    private ServerRegistry serverRegistry;

    public RpcServiceManager(String host, int port) {
        this.host = host;
        this.port = port;
        this.serverRegistry = new NacosServerRegistry();
        autoRegistry();
    }

    public void autoRegistry() {
        String mainClassPath = PackageScanUtils.getStackTrace();
        Class<?> mainClass;
        try {
            mainClass = Class.forName(mainClassPath);
            String scanPath = mainClass.getAnnotation(RpcServerScan.class).value();
            if (StringUtils.isBlank(scanPath)) {
                scanPath = mainClassPath.substring(0, mainClassPath.lastIndexOf("."));
            }
            Set<Class<?>> classes = PackageScanUtils.getClasses(scanPath);
            for (Class<?> cls : classes) {
                if (cls.isAnnotationPresent(RpcServer.class)) {
                    String serverNameValue = cls.getAnnotation(RpcServer.class).name();
                    Object instance = cls.newInstance();
                    if (StringUtils.isBlank(serverNameValue)) {
                        addServer(instance, cls.getCanonicalName());
                    } else {
                        addServer(instance, serverNameValue);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    public <T> void addServer(T server, String serverName) throws NacosException {
        ServiceFactory.addServiceProvider(server, serverName);
        serverRegistry.register(serverName, new InetSocketAddress(host, port));
    }

    public void start() {
        LoggingHandler LOGGING = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        RpcRequestMessageHandler RPC_HANDLER = new RpcRequestMessageHandler();
        HeartbeatServerHandler heartbeatServerHandler = new HeartbeatServerHandler();
        PingMessageHandler PING_MESSAGE_HANDLER = new PingMessageHandler();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(LOGGING);
                            pipeline.addLast(new IdleStateHandler(30, 0, 0));
                            pipeline.addLast(new ProtocolFrameDecoder());
                            pipeline.addLast(MESSAGE_CODEC);
                            pipeline.addLast(heartbeatServerHandler);
                            pipeline.addLast(PING_MESSAGE_HANDLER);
                            pipeline.addLast(RPC_HANDLER);
                        }
                    });

            Channel channel = bootstrap.bind(8080).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("启动服务异常:{}", e.getMessage());
        } finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }
}
