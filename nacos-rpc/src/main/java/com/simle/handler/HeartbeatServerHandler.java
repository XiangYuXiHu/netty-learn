package com.simle.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description
 * @ClassName HeartbeatServerHandler
 * @Author smile
 * @date 2022.03.31 11:05
 */
@Slf4j
public class HeartbeatServerHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleState) {
            IdleState idleState = (IdleState) evt;
            if (IdleState.READER_IDLE == idleState) {
                log.info("长时间没有收到消息，断开连接");
                ctx.close();
            }
            super.userEventTriggered(ctx, evt);
        }
        super.userEventTriggered(ctx, evt);
    }
}
