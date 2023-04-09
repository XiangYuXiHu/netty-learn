package com.simle.loadBalance;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

/**
 * @Description
 * @ClassName RandomRule
 * @Author smile
 * @date 2022.08.27 16:23
 */
public class RandomRule implements LoadBalancer {
    private final Random random = new Random(47);

    @Override
    public Instance getInstance(List<Instance> list) {
        return list.get(random.nextInt(list.size()));
    }
}
