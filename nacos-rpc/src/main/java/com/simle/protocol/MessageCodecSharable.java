package com.simle.protocol;

import com.simle.config.Config;
import com.simle.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * #################################################################################################
 * ##########                  【自定义】消息 编解码 类   【 支持@Sharable 】                   ########
 * ##########   父类 MessageToMessageCodec 认为是完整的信息 【所以必须保证上一个处理器是 帧解码器】 ########
 * #################################################################################################
 * 相当于两个handler合二为一，【既能入站 也能做出站处理】
 * <b>魔数     </b>，用来在第一时间判定是否是无效数据包
 * <b>版本号   </b>，可以支持协议的升级
 * <b>序列化算法</b>，消息正文到底采用哪种序列化反序列化方式，可以由此扩展，例如：json、protobuf、hessian、jdk
 * <b>指令类型  </b>，是登录、注册、单聊、群聊... 跟业务相关
 * <b>请求序号  </b>，为了双工通信，提供异步能力
 * <b>正文长度  </b>
 * <b>消息正文  </b>
 * 写这个类 肯定的认为 上一个处理器 是 帧解码器，所以不用考虑半包黏包问题，直接解码拿数据
 *
 * @author smile
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();

        /**
         * 4字节的 魔数
         */
        out.writeBytes(new byte[]{1, 2, 3, 4});
        out.writeByte(1);
        out.writeByte(Config.getMySerializerAlgorithm().ordinal());
        /**
         * 1字节的 指令类型
         */
        out.writeByte(msg.getMessageType());
        /**
         *  4字节的 请求序号 【大端】
         */
        out.writeInt(msg.getSequenceId());
        /**
         * 1字节的 对其填充，只为了非消息内容 是2的整数倍
         */
        out.writeByte(0xff);

        final byte[] bytes = Config.getMySerializerAlgorithm().serialize(msg);
        out.writeInt(bytes.length);
        // 写入内容
        out.writeBytes(bytes);
        /**
         * 加入List 方便传递给 下一个Handler
         */
        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        /**
         * 大端4字节的 魔数
         */
        int magicNum = in.readInt();
        /**
         * 版本
         */
        byte version = in.readByte();
        /**
         * 0 Java 1 Json
         */
        byte serializerType = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();

        int length = in.readInt();
        final byte[] bytes = new byte[length];
        /**
         * 读取进来，下面再进行 解码
         */
        in.readBytes(bytes, 0, length);

        final MySerializer.Algorithm algorithm = MySerializer.Algorithm.values()[serializerType];
        // 2. 找到消息具体类型
        final Object message = algorithm.deserializer(Message.getMessageClass(messageType), bytes);

        log.info("解码消息内容:{}", message);

        /**
         * 加入List 方便传递给 下一个Handler
         */
        out.add(message);
    }
}
