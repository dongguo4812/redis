package com.dongguo.dianping.entity;

import lombok.Data;
import java.util.Date;

@Data
public class RedisData {
    private Date expireTime;
    private Object data;
}
