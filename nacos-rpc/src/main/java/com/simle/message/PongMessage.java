package com.simle.message;

/**
 * @Description
 * @ClassName PongMessage
 * @Author smile
 * @date 2022.03.06 13:43
 */
public class PongMessage extends Message {
    @Override
    public int getMessageType() {
        return PongMessage;
    }
}
