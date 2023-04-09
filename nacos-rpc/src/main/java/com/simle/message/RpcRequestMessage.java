package com.simle.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;

/**
 * @Description
 * @ClassName RpcRequestMessage
 * @Author smile
 * @date 2022.03.05 22:01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequestMessage extends Message {
    /**
     * 调用接口名称
     */
    private String interfaceName;

    /**
     * 接口方法名
     */
    private String methodName;

    /**
     * 方法返回类型
     */
    private Class<?> returnType;

    /**
     * 方法参数类型
     */
    private Class[] parameterTypes;

    /**
     * 参数值组
     */
    private Object[] parameterValue;

    public RpcRequestMessage(int sequenceId, String interfaceName, String methodName, Class<?> returnType, Class[] parameterTypes, Object[] parameterValue) {
        super.setSequenceId(sequenceId);
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.parameterValue = parameterValue;
    }

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_REQUEST;
    }

    @Override
    public String toString() {
        return "RpcRequestMessage{" +
                "interfaceName='" + interfaceName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", returnType=" + returnType +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", parameterValue=" + Arrays.toString(parameterValue) +
                ", sequenceId=" + sequenceId +
                ", messageType=" + messageType +
                '}';
    }
}
