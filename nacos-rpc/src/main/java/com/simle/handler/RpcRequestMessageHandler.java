package com.simle.handler;

import com.simle.factory.ServiceFactory;
import com.simle.message.RpcRequestMessage;
import com.simle.message.RpcResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @Description
 * @ClassName RpcRequestMessageHandler
 * @Author smile
 * @date 2022.03.27 14:28
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage msg) throws Exception {
        RpcResponseMessage rpcResponseMessage = new RpcResponseMessage();
        rpcResponseMessage.setSequenceId(msg.getSequenceId());
        try {
            Object serviceProvider = ServiceFactory.getServiceProvider(msg.getInterfaceName());
            Method method = serviceProvider.getClass().getMethod(msg.getMethodName(), msg.getParameterTypes());
            Object invoke = method.invoke(serviceProvider, msg.getParameterValue());
            rpcResponseMessage.setReturnValue(invoke);
        } catch (Exception e) {
            log.error("异常:{}", e.getMessage());
            rpcResponseMessage.setExceptionValue(new Exception(e.getMessage()));
        } finally {
            ctx.writeAndFlush(rpcResponseMessage);
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * 读空闲
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
//    @Override
//    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        if (evt instanceof IdleStateEvent) {
//            IdleState state = ((IdleStateEvent) evt).state();
//            if (state == IdleState.READER_IDLE) {
//                log.info("长时间未收到心跳包，断开连接...");
//                ctx.close();
//            }
//        } else {
//            super.userEventTriggered(ctx, evt);
//        }
//    }
}
