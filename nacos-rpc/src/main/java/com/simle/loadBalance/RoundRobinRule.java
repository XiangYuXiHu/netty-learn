package com.simle.loadBalance;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description
 * @ClassName RoundRobinRule
 * @Author smile
 * @date 2022.08.27 16:25
 */
public class RoundRobinRule implements LoadBalancer {

    private AtomicInteger counter = new AtomicInteger(0);

    public int getAndIncrement() {
        int current;
        int next;
        do {
            current = counter.get();
            next = current >= Integer.MAX_VALUE ? 0 : current + 1;
        } while (!counter.compareAndSet(current, next));
        return current;
    }

    @Override
    public Instance getInstance(List<Instance> list) {
        int pos = getAndIncrement() % list.size();
        return list.get(pos);
    }
}
