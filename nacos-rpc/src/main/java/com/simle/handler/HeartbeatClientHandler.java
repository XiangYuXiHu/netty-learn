package com.simle.handler;

import com.simle.id.SequenceIdGenerator;
import com.simle.message.Message;
import com.simle.message.PingMessage;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description
 * @ClassName HeartBeatClientHandler
 * @Author smile
 * @date 2022.03.31 10:52
 */
@Slf4j
public class HeartbeatClientHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (IdleState.WRITER_IDLE == idleStateEvent.state()) {
                log.info("发送心跳报文:{}", ctx.channel().remoteAddress());
                PingMessage pingMessage = new PingMessage();
                pingMessage.setSequenceId(SequenceIdGenerator.nextId());
                pingMessage.setMessageType(Message.PingMessage);
                ctx.writeAndFlush(pingMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("远程调用异常:{}", cause.getMessage());
        ctx.close();
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channel unregistered");
        ctx.close();
        super.channelUnregistered(ctx);
    }
}
