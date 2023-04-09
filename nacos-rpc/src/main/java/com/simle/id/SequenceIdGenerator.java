package com.simle.id;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description
 * @ClassName SequenceIdGenerator
 * @Author smile
 * @date 2022.03.27 16:01
 */
public class SequenceIdGenerator {

    private static final AtomicInteger id = new AtomicInteger();

    public static int nextId() {
        return id.incrementAndGet();
    }
}
