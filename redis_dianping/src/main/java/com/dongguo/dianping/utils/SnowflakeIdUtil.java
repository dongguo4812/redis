package com.dongguo.dianping.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.dongguo.dianping.support.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.Collections;

/**
 * 雪花算法 生成唯一id
 * 项目启动后，只在第一次调用时生成SNOWFLAKE
 * @Author: Administrator
 * @Date: 2023-05-10
 */
@Slf4j
public class SnowflakeIdUtil {
    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(getWorkerId(RedisConstants.CACHE_SNOWFLAKE_WORKID_key), 1);


    /**
     * 生成/获取workid的lua脚本所在路径
     */
    private final static String REDIS_WORKID_PATH = "redis/redis_work_id.lua";

    /**
     * 公用获取long类型的id，雪花算法
     * @return id
     */
    public static Long getNextId(){
        return SNOWFLAKE.nextId();
    }
    /**
     *
     * 容器环境 缓存中获取WorkerId 并更新
     *  String luaStr =
     *  "local isExist = redis.call('exists', KEYS[1])\n" + "if isExist == 1 then\n" + "    local workerId = redis.call('get', KEYS[1])\n" + "workerId =(workerId + 1) % 1024\n" + "    redis.call('set', KEYS[1], workerId)\n" + "    return workerId\n" + "else\n" + "    redis.call('set', KEYS[1], 0)\n" + "    return 0\n" + "end";
     *
     * @param key
     * @return
     */
    private static Long getWorkerId(String key) {
        StringRedisTemplate stringRedisTemplate = ApplicationContextHolder.getBean(StringRedisTemplate.class);
        try {
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(REDIS_WORKID_PATH)));
            redisScript.setResultType(Long.class);
            Long workerId = stringRedisTemplate.execute(redisScript, Collections.singletonList(key));
            log.info("分配的workerId:" + workerId);
            return workerId;
        } catch (Exception e) {
            log.info("分配workerId失败  捕捉异常！" + e.getMessage());
            e.printStackTrace();
        }
        log.info("分配workerId失败！");
        return 1L;
    }
}
