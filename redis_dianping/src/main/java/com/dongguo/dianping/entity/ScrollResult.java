package com.dongguo.dianping.entity;

import lombok.Data;

import java.util.List;

/**
 * 滚动分页封装类
 */
@Data
public class ScrollResult {
    private List<?> list;
    private Long minTime;
    private Integer offset;
}
