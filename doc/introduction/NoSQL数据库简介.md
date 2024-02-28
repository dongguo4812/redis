# NoSQL数据库概述

NoSQL( non-relational)  非关系数据库，也有理解NoSQL = Not Only SQL ，意即“不仅仅是SQL”。

NoSQL 不依赖业务逻辑方式存储，而以简单的key-value模式存储，因此大大的增加了数据库的扩展能力。

# 常见的NoSQL数据库

我将目前的NoSQL分为四大类

## 一、键值(key-value)存储数据库

### Memcache

| <img src="https://gitee.com/dongguo4812_admin/image/raw/master/image/202402282006699.png" alt="image-20240228194656308" style="zoom:150%;" /> | 很早出现的NoSql数据库<br/>数据都在内存中，一般不持久化<br/> 支持简单的key-value模式，支持类型单一<br/>一般是作为缓存数据库辅助持久化的数据库 |
| ------------------------------------------------------------ | ------------------------------------------------------------ |

### Redis

| <img src="https://gitee.com/dongguo4812_admin/image/raw/master/image/202402282006150.png" alt="image-20240228195104912" style="zoom:150%;" /> | 几乎覆盖了Memcached的绝大部分功能<br/>数据都在内存中，支持持久化，主要用作备份恢复<br/>除了支持简单的key-value模式，还支持多种数据结构的存储，比如  list、set、hash、zset等。<br/>一般是作为缓存数据库辅助持久化的数据库 |
| ------------------------------------------------------------ | ------------------------------------------------------------ |

## 二、文档型数据库

### MongoDB

| <img src="https://gitee.com/dongguo4812_admin/image/raw/master/image/202402282006118.png" alt="image-20240228195227769" style="zoom:150%;" /> | 高性能、开源、模式自由(schema free)的**文档型数据库**<br/>数据都在内存中， 如果内存不足，把不常用的数据保存到硬盘<br/>虽然是key-value模式，但是对value（尤其是**json**）提供了丰富的查询功能<br/>支持二进制数据及大型对象<br/>可以根据数据的特点**替代RDBMS** ，成为独立的数据库。或者配合RDBMS，存储特定的数据。 |
| ------------------------------------------------------------ | ------------------------------------------------------------ |

## 三、列式存储数据库

### ClickHouse

ClickHouse是一个开源的，面向列的MPP架构数据分析数据库（大规模并行处理）。ClickHouse对实时查询处理的支持使其适用于需要亚秒级分析结果的应用程序。

<img src="https://gitee.com/dongguo4812_admin/image/raw/master/image/202402282006222.png" alt="img" style="zoom:50%;" />

### Hbase

HBase是Hadoop项目中的数据库。它用于需要对大量的数据进行随机、实时的读写操作的场景中。

HBase的目标就是处理数据量非常庞大的表，可以用普通的计算机处理超过10亿行数据，还可处理有数百万列元素的数据表。

<img src="https://gitee.com/dongguo4812_admin/image/raw/master/image/202402282006837.png" alt="image-20240228200255137" style="zoom:150%;" />

### Cassandra

Apache Cassandra是一款免费的开源NoSQL数据库，其设计目的在于管理由大量商用服务器构建起来的庞大集群上的海量数据集(数据量通常达到PB级别)。在众多显著特性当中，Cassandra最为卓越的长处是对写入及读取操作进行规模调整，而且其不强调主集群的设计思路能够以相对直观的方式简化各集群的创建与扩展流程。

<img src="https://gitee.com/dongguo4812_admin/image/raw/master/image/202402282006558.png" alt="image-20240228200315207" style="zoom:150%;" />

## 四、图关系型数据库

### Neo4j

Neo4j是用Java实现的开源NoSQL图数据库。如：网络管理、软件分析、组织和项目管理、社交项目等方面。

<img src="https://gitee.com/dongguo4812_admin/image/raw/master/image/202402282006751.png" alt="image-20240228200356807" style="zoom:150%;" />