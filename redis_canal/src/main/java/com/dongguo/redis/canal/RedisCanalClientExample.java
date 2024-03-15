package com.dongguo.redis.canal;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.CanalEntry.*;
import com.alibaba.otter.canal.protocol.Message;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class RedisCanalClientExample {
    @Resource
    private RedisTemplate redisTemplate;
    public static final Integer _60SECONDS = 60;
    public static final String CANAL_IP_ADDR = "192.168.122.131";

    private void redisInsert(List<Column> columns) {
        JSONObject jsonObject = new JSONObject();
        for (CanalEntry.Column column : columns) {
            System.out.println(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
            jsonObject.put(column.getName(), column.getValue());
        }
        if (columns.size() > 0) {
            redisTemplate.opsForValue().set(columns.get(0).getValue(), jsonObject.toJSONString());
        }
    }

    private void redisDelete(List<CanalEntry.Column> columns) {
        JSONObject jsonObject = new JSONObject();
        for (CanalEntry.Column column : columns) {
            jsonObject.put(column.getName(), column.getValue());
        }
        if (columns.size() > 0) {
            redisTemplate.delete(columns.get(0).getValue());

        }
    }

    private void redisUpdate(List<CanalEntry.Column> columns) {
        JSONObject jsonObject = new JSONObject();
        for (CanalEntry.Column column : columns) {
            System.out.println(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
            jsonObject.put(column.getName(), column.getValue());
        }
        if (columns.size() > 0) {
            redisTemplate.opsForValue().set(columns.get(0).getValue(), jsonObject.toJSONString());

            System.out.println("---------update after: " + jsonObject.toJSONString());

        }
    }

    public void printEntry(List<CanalEntry.Entry> entries) {
        for (CanalEntry.Entry entry : entries) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }

            CanalEntry.RowChange rowChage = null;
            try {
                //获取变更的row数据
                rowChage = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error,data:" + entry.toString(), e);
            }
            //获取变动类型
            CanalEntry.EventType eventType = rowChage.getEventType();
            System.out.println(String.format("================&gt; binlog[%s:%s] , name[%s,%s] , eventType : %s",
                    entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                    entry.getHeader().getSchemaName(), entry.getHeader().getTableName(), eventType));

            for (CanalEntry.RowData rowData : rowChage.getRowDatasList()) {
                if (eventType == CanalEntry.EventType.INSERT) {
                    redisInsert(rowData.getAfterColumnsList());
                } else if (eventType == CanalEntry.EventType.DELETE) {
                    redisDelete(rowData.getBeforeColumnsList());
                } else {//EventType.UPDATE
                    redisUpdate(rowData.getAfterColumnsList());
                }
            }
        }
    }

    @PostConstruct
    public void init() {
        System.out.println("--------------init()--------------");

        // 创建链接canal服务器
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(CANAL_IP_ADDR, 11111),
                "example",
                "",
                "");
        int batchSize = 1000;
        //空闲空转计数器
        int emptyCount = 0;
        System.out.println("--------------canal init ok, 开始监听mysql变化--------------");
        try {
            connector.connect();
            connector.subscribe("redis.t_user");
            connector.rollback();
            int totalEmptyCount = 10 * _60SECONDS;
            while (emptyCount < totalEmptyCount) {
                System.out.println("我是canal，每秒一次正在监听：" + UUID.randomUUID().toString());
                //获取指定数量的数据
                Message mes = connector.getWithoutAck(batchSize);
                long batchId = mes.getId();
                int size = mes.getEntries().size();
                if (batchId == -1 || size == 0) {
                    emptyCount++;
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    //计数器重新置零
                    emptyCount = 0;
                    printEntry(mes.getEntries());
                }
                //提交确认
                connector.ack(batchId);
            }
            System.out.println("已经监听了" + totalEmptyCount + "秒，无任何消息，请重启重试......");
        } finally {
            connector.disconnect();
        }
    }
}
