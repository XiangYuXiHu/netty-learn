package com.simle.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @Description
 * @ClassName ProcotolFrameDecoder
 * @Author smile
 * @date 2022.03.06 10:20
 */
public class ProtocolFrameDecoder extends LengthFieldBasedFrameDecoder {

    public ProtocolFrameDecoder() {
        super(1024, 12, 4, 0, 0);
    }

    public ProtocolFrameDecoder(int maxFrameLength,
                                int lengthFieldOffset,
                                int lengthFieldLength,
                                int lengthAdjustment,
                                int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
