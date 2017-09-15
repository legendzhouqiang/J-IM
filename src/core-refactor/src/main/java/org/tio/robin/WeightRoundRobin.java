package org.tio.robin;

import lombok.extern.slf4j.Slf4j;
import org.tio.common.misc.TioException;
import org.tio.concurrent.ListWithLock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/25
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 加权轮询算法
 */
@Slf4j
public class WeightRoundRobin implements IWeightRoundRobin{

    private ListWithLock<Node> arr;

    private int currentIndex;
    private int currentWeight;

    private int total;
    private int maxWeight;
    private int cdWeight;

    private static Map<String, WeightRoundRobin> cache = new HashMap<>();

    private WeightRoundRobin() {
        arr = new ListWithLock<>(new ArrayList<Node>());
        ReadWriteLock lock = arr.getLock();
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            total = arr.getObj().size();
            maxWeight = maxWeight();
            cdWeight = commonDivisor();
        } finally {
            writeLock.unlock();
        }
    }

    public static WeightRoundRobin newInstance(String cacheKey) throws TioException {
        if (cacheKey == null) {
            log.warn("Can not input null cacheKey for Round IRobin Cache.");
            throw new TioException(TioException.ExceptionCodeEnum.Null_Input_Error.code);
        }
        WeightRoundRobin roundRobin = cache.get(cacheKey);
        if (roundRobin == null) {
            roundRobin = new WeightRoundRobin();
            cache.put(cacheKey, roundRobin);
        }
        return roundRobin;
    }

    @Override
    public IWeightRoundRobin expand(String val, int weight) {
        ReadWriteLock lock = arr.getLock();
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            Node node = new Node(val, weight);
            List<Node> obj = arr.getObj();
            obj.add(node);
            total = obj.size();
            maxWeight = maxWeight();
            cdWeight = commonDivisor();
        } finally {
            writeLock.unlock();
        }
        return this;
    }

    @Override
    public IWeightRoundRobin shrink(String val) {
        ReadWriteLock lock = arr.getLock();
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            List<Node> obj = arr.getObj();
            obj.remove(new Node(val, 0));
            total = obj.size();
            maxWeight = maxWeight();
            cdWeight = commonDivisor();
        } finally {
            writeLock.unlock();
        }
        return this;
    }

    private int commonDivisor() {
        int comDivisor = 0;
        ReadWriteLock lock = arr.getLock();
        for (int i = 0; i < total - 1; i++) {
            if (comDivisor == 0) {
                comDivisor = gcd(arr.getObj().get(i).weight, arr.getObj().get(i + 1).weight);
            } else {
                comDivisor = gcd(comDivisor, arr.getObj().get(i + 1).weight);
            }
        }
        return comDivisor;
    }

    private int gcd(int m, int n) {
        while (m % n != 0) {
            int temp = m % n;
            m = n;
            n = temp;
        }
        return n;
    }


    private int maxWeight() {
        int max = 0;
        List<Node> obj = arr.getObj();
        for (int i = 1; i < obj.size(); i++) {
            max = max < obj.get(i).weight ? obj.get(i).weight : max;
        }
        return max;
    }

    @Override
    public String round() {
        if (maxWeight == 0) {
            return null;
        }
        ReadWriteLock lock = arr.getLock();
        Lock readLock = lock.readLock();
        try {
            readLock.lock();
            while (true) {
                currentIndex = (currentIndex + 1) % total;
                if (currentIndex == 0) {
                    currentWeight = currentWeight - cdWeight;
                    if (currentWeight <= 0) {
                        currentWeight = maxWeight;
                    }
                }
                if (arr.getObj().get(currentIndex).weight >= currentWeight) {
                    return arr.getObj().get(currentIndex).value;
                }
            }
        } finally {
            readLock.unlock();
        }
    }

    public static void main(String[] args) {
        WeightRoundRobin weightRoundRobin = new WeightRoundRobin();
        weightRoundRobin.expand("a", 10).expand("b", 15).expand("c", 20);
        for (int i=0;i<20;i++) {
            String round = weightRoundRobin.round();
            System.out.println(round);
        }
    }

    private class Node {
        String value;
        int weight;

        Node(String value, int weight) {
            this.value = value;
            this.weight = weight;
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj instanceof Node) {
                Node other = (Node) obj;
                return this.value.equals(other.value);
            }
            return false;
        }
    }
}
