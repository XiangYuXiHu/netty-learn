package com.simle.protocol;

import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @Description
 * @ClassName MySerializer
 * @Author smile
 * @date 2022.03.05 22:04
 */
public interface MySerializer {

    /**
     * 反序列化
     *
     * @param clazz
     * @param bytes
     * @param <T>
     * @return
     */
    <T> T deserializer(Class<T> clazz, byte[] bytes);

    /**
     * 序列化
     *
     * @param object
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T object);

    enum Algorithm implements MySerializer {
        Java {
            @Override
            public <T> T deserializer(Class<T> clazz, byte[] bytes) {
                try {
                    ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                    ObjectInputStream ois = new ObjectInputStream(bis);

                    T message = (T) ois.readObject();
                    return message;
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException("反序列化算法失败！");
                }
            }

            @Override
            public <T> byte[] serialize(T object) {
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(object);

                    return bos.toByteArray();
                } catch (IOException e) {
                    throw new RuntimeException("序列化算法失败！", e);
                }
            }
        },
        Json {
            @Override
            public <T> T deserializer(Class<T> clazz, byte[] bytes) {
                String json = new String(bytes, StandardCharsets.UTF_8);
                return JSONObject.parseObject(json, clazz);
            }

            @Override
            public <T> byte[] serialize(T object) {
                String json = JSONObject.toJSONString(object);
                return json.getBytes(StandardCharsets.UTF_8);
            }
        }
    }
}
