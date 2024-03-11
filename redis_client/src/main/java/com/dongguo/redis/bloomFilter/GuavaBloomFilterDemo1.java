package com.dongguo.redis.bloomFilter;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
/**
 * @Author: Administrator
 * @Date: 2024-02-26
 * Guava布隆过滤器demo
 */
public class GuavaBloomFilterDemo1 {
    public static void main(String[] args) {
        /**
         * 创建一个布隆过滤器
         * Funnels.integerFunnel()用于将对象转换为适合布隆过滤器的位序列的转换函数。
         * 预计插入的元素数量为100
         */
        BloomFilter<Integer> filter = BloomFilter.create(Funnels.integerFunnel(), 100);
        //判断指定元素是否存在
        System.out.println(filter.mightContain(1));
        System.out.println(filter.mightContain(2));
        //将元素添加到布隆过滤器
        filter.put(1);
        filter.put(2);
        //判断指定元素是否存在
        System.out.println(filter.mightContain(1));
        System.out.println(filter.mightContain(2));
    }
}
