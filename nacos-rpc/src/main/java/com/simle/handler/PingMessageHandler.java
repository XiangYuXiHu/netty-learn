package com.simle.handler;

import com.simle.message.PingMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description
 * @ClassName PingMessageHandler
 * @Author smile
 * @date 2022.03.31 17:40
 */
@Slf4j
public class PingMessageHandler extends SimpleChannelInboundHandler<PingMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PingMessage msg) throws Exception {
        log.info("接收心跳消息:{}", msg);
    }
}
