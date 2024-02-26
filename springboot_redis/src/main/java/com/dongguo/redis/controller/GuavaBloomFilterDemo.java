package com.dongguo.redis.controller;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Administrator
 * @Date: 2024-02-26
 * Guava布隆过滤器demo
 */
public class GuavaBloomFilterDemo {
    private static final int COUNT = 10000 * 100;
    //误判率  大于0小于1.0
    private static final double FPP = 0.01;

    public static void main(String[] args) {
//        bloomFilter1();
        bloomFilter2();
    }

    /**
     * demo
     */
    private static void bloomFilter1() {
        BloomFilter<Integer> filter = BloomFilter.create(Funnels.integerFunnel(), 100);
        System.out.println(filter.mightContain(1));
        System.out.println(filter.mightContain(2));
        filter.put(1);
        filter.put(2);
        System.out.println(filter.mightContain(1));
        System.out.println(filter.mightContain(2));
    }

    private static void bloomFilter2() {
        //误判率设置为0.01
        BloomFilter<Integer> filter = BloomFilter.create(Funnels.integerFunnel(), COUNT, FPP);
        //插入100万样本
        for (int i = 0; i < COUNT; i++) {
            filter.put(i);
        }

        List<Integer> list = new ArrayList<>(COUNT);
//        for (int i = 0; i < COUNT; i++) {
//            boolean contain = filter.mightContain(i);
//            if (contain){
//                list.add(i);
//            }
//        }
        //1000001 至 10100000  都不在布隆过滤器中，查看误判率
        for (int i = COUNT + 1; i < COUNT + 100000; i++) {
            boolean contain = filter.mightContain(i);
            if (contain) {
                System.out.println("发生了误判:" + i);
                list.add(i);
            }
        }
        System.out.println("布隆过滤器误判的数量：" + list.size());
    }
}
