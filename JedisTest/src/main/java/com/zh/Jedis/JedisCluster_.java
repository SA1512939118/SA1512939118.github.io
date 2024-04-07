package com.zh.Jedis;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zh
 * @version 1.0
 * @date 2024/4/7 20:31
 */
public class JedisCluster_ {

    /**
     * 1.set可以加入多个入口
     * 2.使用集群需要将所有相关的端口都打开
     * 3.构造方法有很多，也可以直接传入地址
     * @param args
     */
    public static void main(String[] args) throws IOException {
        JedisPoolConfig config = new JedisPoolConfig();
        config .setMaxTotal(500);
        config .setMinIdle(2);
        config .setMaxIdle(500);
        config .setMaxWaitMillis(10000);
        config .setTestOnBorrow(true);
        config .setTestOnReturn(true);
        Set<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort("x.x.x.x", 6379));
//        nodes.add(new HostAndPort("x.x.x.x", 6380));
//        nodes.add(new HostAndPort("x.x.x.x", 6381));
//        nodes.add(new HostAndPort("x.x.x.x", 6382));
        JedisCluster jedisCluster = new JedisCluster(nodes, 10000, 10000, 100, "password", config);
        jedisCluster.set("address","北京");
        System.out.println(jedisCluster.get("address"));
    }
}
