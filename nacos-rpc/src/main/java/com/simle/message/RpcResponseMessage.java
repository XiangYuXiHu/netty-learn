package com.simle.message;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @ClassName RpcResponseMessage
 * @Author smile
 * @date 2022.03.27 14:25
 */
@Getter
@Setter
public class RpcResponseMessage extends Message {

    private Object returnValue;

    private Exception exceptionValue;

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }

    @Override
    public String toString() {
        return "RpcResponseMessage{" +
                "returnValue=" + returnValue +
                ", exceptionValue=" + exceptionValue +
                ", sequenceId=" + sequenceId +
                ", messageType=" + messageType +
                '}';
    }
}
