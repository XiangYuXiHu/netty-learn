package com.simle.config;

import com.simle.protocol.MySerializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Description
 * @ClassName Config
 * @Author smile
 * @date 2022.03.06 10:40
 */
public abstract class Config {
    static Properties properties;

    static {
        try (InputStream in = Config.class.getResourceAsStream("/application.properties")) {
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getServerPort() {
        String value = properties.getProperty("server.port");
        if (null == value) {
            return 8080;
        } else {
            return Integer.parseInt(value);
        }
    }

    public static MySerializer.Algorithm getMySerializerAlgorithm() {
        String value = properties.getProperty("mySerializer.algorithm");
        if (null == value) {
            return MySerializer.Algorithm.Java;
        } else {
            return MySerializer.Algorithm.valueOf(value);
        }
    }
}
