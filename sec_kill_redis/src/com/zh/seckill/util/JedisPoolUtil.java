package com.zh.seckill.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author zh
 * @version 1.0
 * @date 2024/4/7 0:26
 */
public class JedisPoolUtil {

    //volatile关键字作用
    //1.保证共享变量的可见性，当一个线程去修改一个共享变量时，另外一个线程可以读取这个修改的值
    //2.保证顺序的一致性，禁止指令重排
    private static volatile JedisPool jedisPool = null;


    private JedisPoolUtil(){

    }

    //保证单例
    public static JedisPool getJedisPoolInstance(){
        if (null == jedisPool){

            synchronized (JedisPoolUtil.class){
                if (null == jedisPool){
                    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

                    jedisPoolConfig.setMaxTotal(200);
                    jedisPoolConfig.setMaxIdle(32);
                    jedisPoolConfig.setMaxWaitMillis(60 * 1000);
                    jedisPoolConfig.setBlockWhenExhausted(true);
                    jedisPoolConfig.setTestOnBorrow(true);

                    jedisPool = new JedisPool(jedisPoolConfig,"192.168.198.135",6379,60000,"foobared");
                }
            }
        }
        return jedisPool;
    }

    //释放链接资源
    public static void release(Jedis jedis){
        if (null != jedis){
            jedis.close();//如果这个jedis是从连接池获取的，这里就会将他释放回连接池
        }
    }
}
