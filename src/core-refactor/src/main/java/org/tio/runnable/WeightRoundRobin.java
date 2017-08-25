package org.tio.runnable;

import org.tio.concurrent.ListWithLock;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/25
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public class WeightRoundRobin {

    private ListWithLock<Node> arr;

    private int currentIndex;
    private int currentWeight;

    private int total;
    private int maxWeight;
    private int gcdWeight;

    private WeightRoundRobin() {
        ReadWriteLock lock = arr.getLock();
        Lock readLock = lock.readLock();
        try {
            total = arr.getObj().size();
        } finally {
            readLock.unlock();
        }
    }

    private int commonDivisor() {
        int comDivisor = 0;
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
        ReadWriteLock lock = arr.getLock();
        Lock readLock = lock.readLock();
        try {
            readLock.lock();
            List<Node> obj = arr.getObj();
            for (int i = 1; i < obj.size(); i++) {
                max = max < obj.get(i).weight ? obj.get(i).weight : max;
            }
        } finally {
            readLock.unlock();
        }
        return max;
    }

    public String round() {
        while (true) {
            currentIndex = (currentIndex + 1) % total;
            if (currentIndex == 0) {
                currentWeight = currentWeight - gcdWeight;
                if (currentWeight <= 0) {
                    currentWeight = maxWeight;
                    if(currentWeight == 0) {
                        return null;
                    }
                }
            }

            if(arr.getObj().get(currentIndex).weight >= currentWeight) {
                return arr.getObj().get(currentIndex).value;
            }
        }
    }

    class Node {
        String value;
        int weight;
    }
}
