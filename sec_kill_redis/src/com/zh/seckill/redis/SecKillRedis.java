package com.zh.seckill.redis;

import com.zh.seckill.util.JedisPoolUtil;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.List;

/**
 * @author zh
 * @version 1.0
 * @date 2024/4/6 18:30
 */
public class SecKillRedis {

    @Test
    public void testRedis(){
        Jedis jedis = new Jedis("192.168.198.135", 6379);
        jedis.auth("foobared");
        System.out.println("jedis.ping() = " + jedis.ping());
        jedis.close();
    }

    /**
     *
     * @param uid 用户id，后台生成
     * @param ticketNo 票的编号，比如北京-成都的ticket就是bj_cd
     * @return
     */

    public static boolean doSecKill(String uid, String ticketNo){
        //uid和ticketNo进行非空校验
        if (uid == null || ticketNo == null){
            return false;
        }
//        Jedis jedis = new Jedis("192.168.198.135", 6379);
//        jedis.auth("foobared");

        JedisPool jedisPool = JedisPoolUtil.getJedisPoolInstance();
        Jedis jedis = jedisPool.getResource();
        System.out.println("使用的是连接池技术");

        //拼接票的库存key
        String stockKey = "sk:" + ticketNo + ":ticket";

        //拼接抢到票的用户集合的key
        String userKey = "sk:" + ticketNo + ":user";


        //监控库存
        jedis.watch(stockKey);

        String stock = jedis.get(stockKey);
        if (stock == null){
            System.out.println("秒杀还未开始，请稍后再试");
            jedis.close();
            return false;
        }

        if (jedis.sismember(userKey,uid)){
            System.out.println("请勿重复购买");
            jedis.close();;
            return false;
        }

        //判断火车票，是否还有剩余
        if (Integer.parseInt(stock) <= 0){
            System.out.println("票已售罄");
            jedis.close();
            return false;
        }

//        jedis.decr(stockKey);
//        jedis.sadd(userKey,uid);//将用户添加进秒杀成功的集合中，redis的set集合天然去重

        //使用事务完成秒杀
        Transaction multi = jedis.multi();
        multi.decr(stockKey);
        multi.sadd(userKey,uid);

        List<Object> result = multi.exec();
        if (result == null || result.size() == 0){
            System.out.println("抢票失败");
        }

        System.out.println(uid + "秒杀成功");
        jedis.close();
        return true;

    }
}
