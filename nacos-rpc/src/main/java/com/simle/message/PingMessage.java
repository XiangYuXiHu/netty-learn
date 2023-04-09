package com.simle.message;

/**
 * @Description
 * @ClassName PingMessage
 * @Author smile
 * @date 2022.03.06 13:42
 */
public class PingMessage extends Message {
    @Override
    public int getMessageType() {
        return PingMessage;
    }

    @Override
    public String toString() {
        return "PingMessage{" +
                "sequenceId=" + sequenceId +
                ", messageType=" + messageType +
                '}';
    }
}
