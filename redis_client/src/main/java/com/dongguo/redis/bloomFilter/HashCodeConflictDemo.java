package com.dongguo.redis.bloomFilter;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: Administrator
 * @Date: 2024-02-26
 * hash冲突demo
 */
public class HashCodeConflictDemo {
    public static void main(String[] args) {
        System.out.println("Aa".hashCode());//2112
        System.out.println("BB".hashCode());//2112
        System.out.println("柳柴".hashCode());//851553
        System.out.println("柴柕".hashCode());//851553

        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < 200000; i++) {
            int hashCode = new Object().hashCode();
            if (set.contains(hashCode)){
                System.out.println("出现了重复的hashcode: "+hashCode+"\t 运行到"+i);//出现了重复的hashcode: 2134400190  运行到105488
                break;
            }
            set.add(hashCode);
        }
    }
}
