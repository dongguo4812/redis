package com.dongguo.redis.bloomFilter;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Administrator
 * @Date: 2024-02-26
 * Guava布隆过滤器demo
 */
public class GuavaBloomFilterDemo2 {
    //布隆过滤器预计插入的元素数量为100W
    private static final int COUNT = 10000 * 100;

    public static void main(String[] args) {

        BloomFilter<Integer> filter = BloomFilter.create(Funnels.integerFunnel(), COUNT);
        //插入100万样本
        for (int i = 0; i < COUNT; i++) {
            filter.put(i);
        }
        List<Integer> list = new ArrayList<>(COUNT);
        for (int i = 0; i < COUNT; i++) {
            boolean contain = filter.mightContain(i);
            if (contain){
                list.add(i);
            }
        }
        System.out.println("布隆过滤器元素存在的数量：" + list.size());
    }
}
