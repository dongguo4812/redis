package com.dongguo.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;

/**
 * redis序列化工具类
 */

@Configuration
public class RedisConfig {


    /**
     * @param lettuceConnectionFactory
     * @return redis序列化的工具配置类
     * 1) "order:696"  序列化过
     * 2) "\xac\xed\x00\x05t\x00\order:696"   没有序列化过
     */
    @Bean
    public RedisTemplate<String, Serializable> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, Serializable> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        //设置key序列化方式string
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //设置value的序列化方式json
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }


/*    @Bean
    public DefaultClientResources lettuceClientResources() {
        return DefaultClientResources.create();
    }
    */

    /**
     * 定时扫描Redis集群的拓扑变化，自动更新本地的节点信息。
     *
     * @return
     *//*
    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory(RedisProperties redisProperties, ClientResources clientResources) {
        ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                .enablePeriodicRefresh(Duration.ofSeconds(30))//按照周期刷新拓扑
                .enableAllAdaptiveRefreshTriggers()//根据事件刷新拓扑
                .build();
        ClusterClientOptions clusterClientOptions = ClusterClientOptions.builder()
                //redis命令超时时间，超时后才会使用新的拓扑信息重新建立连接
                .timeoutOptions(TimeoutOptions.enabled(Duration.ofSeconds(10)))
                .topologyRefreshOptions(topologyRefreshOptions)
                .build();
        LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
                .clientResources(clientResources)
                .clientOptions(clusterClientOptions)
                .build();
        RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration(redisProperties.getCluster().getNodes());
        clusterConfig.setMaxRedirects(redisProperties.getCluster().getMaxRedirects());
        clusterConfig.setPassword(RedisPassword.of(redisProperties.getPassword()));

        return new LettuceConnectionFactory(clusterConfig, clientConfiguration);
    }*/

    /**
     * 单机配置
     * @return
     */
    @Bean
    public Redisson redisson() {
        Config config = new Config();
        config.useSingleServer()
                // use "rediss://" for SSL connection
                .setAddress("redis://192.168.122.131:6379").setPassword("root");
        return (Redisson) Redisson.create(config);
    }


    /**
     * RedLock 3个各自独立的master
     * @return
     */
    @Bean("redissonClient1")
    RedissonClient redissonClient1(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.122.132:7001").setPassword("root");
        return Redisson.create(config);
    }
    @Bean("redissonClient2")
    RedissonClient redissonClient2(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.122.133:7002").setPassword("root");
        return Redisson.create(config);
    }

    @Bean("redissonClient3")
    RedissonClient redissonClient3(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.122.134:7003").setPassword("root");
        return Redisson.create(config);
    }
}

