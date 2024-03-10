package com.dongguo.redis.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import static com.dongguo.redis.utils.CacheKeyUtil.CACHE_GAME_SIGN_KEY;

@Slf4j
@Service
public class GameSignService {
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 以日期为key,玩家id为offset
     */
    public void addGamer() {
        String oneKey = CACHE_GAME_SIGN_KEY + "20240304";
        redisTemplate.opsForValue().setBit(oneKey, 1001, Boolean.TRUE);
        redisTemplate.opsForValue().setBit(oneKey, 1002, Boolean.TRUE);
        redisTemplate.opsForValue().setBit(oneKey, 1003, Boolean.TRUE);

        String twoKey = CACHE_GAME_SIGN_KEY + "20240305";
        redisTemplate.opsForValue().setBit(twoKey, 1001, Boolean.TRUE);
        redisTemplate.opsForValue().setBit(twoKey, 1003, Boolean.TRUE);

        String threeKey = CACHE_GAME_SIGN_KEY + "20240306";
        redisTemplate.opsForValue().setBit(threeKey, 1001, Boolean.TRUE);
        redisTemplate.opsForValue().setBit(threeKey, 1002, Boolean.TRUE);
        redisTemplate.opsForValue().setBit(threeKey, 1003, Boolean.TRUE);

    }

    public Long getSignThreeDayCount() {
        String destKey = CACHE_GAME_SIGN_KEY + "destKey";
        String oneKey = CACHE_GAME_SIGN_KEY + "20240304";
        String twoKey = CACHE_GAME_SIGN_KEY + "20240305";
        String threeKey = CACHE_GAME_SIGN_KEY + "20240305";
        //取交集将结果保存到destKey中
        bitOp("AND", destKey, oneKey, twoKey, threeKey);
        //获取destKey中1的个数
        return bitCount(destKey);
    }

    /**
     * @param operation  位操作类型 AND、OR、XOR 和 NOT
     * @param destKey    用于存储执行位操作后的结果
     * @param sourceKeys 源键（source keys）的列表
     */
    public void bitOp(String operation, String destKey, String... sourceKeys) {
        // 构建并执行原生 Redis 命令
        Object count = redisTemplate.execute((RedisCallback<Long>) connection -> {
            // 转换操作名、目标键和源键为字节数组
            byte[] opBytes = operation.getBytes();
            byte[] destKeyBytes = destKey.getBytes();
            byte[][] sourceKeyBytes = new byte[sourceKeys.length][];
            for (int i = 0; i < sourceKeys.length; i++) {
                sourceKeyBytes[i] = sourceKeys[i].getBytes();
            }

            // 合并操作字节数组和所有键的字节数组
            byte[][] args = new byte[sourceKeyBytes.length + 2][];
            args[0] = opBytes;
            args[1] = destKeyBytes;
            System.arraycopy(sourceKeyBytes, 0, args, 2, sourceKeyBytes.length);

            // 执行 BITOP 命令
            // 注意：这里我们直接传递命令名称 "BITOP" 和参数数组
            return (Long) connection.execute("BITOP", args);
        });
    }

    /**
     * 计算key中1的个数
     * @param key
     * @return
     */
    public Long bitCount(String key) {
        return (Long) redisTemplate.execute((RedisCallback<Long>) connection -> {
            // 转换键为字节数组
            byte[] keyBytes = key.getBytes();
            // 执行 BITCOUNT 命令
            return connection.bitCount(keyBytes);
        });
    }
}
