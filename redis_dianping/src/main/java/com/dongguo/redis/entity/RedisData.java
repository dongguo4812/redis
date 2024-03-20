package com.dongguo.redis.entity;

import lombok.Data;
import java.util.Date;

@Data
public class RedisData {
    private Date expireTime;
    private Object data;
}
