package com.zh.Jedis;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zh
 * @version 1.0
 * @date 2024/3/27 14:52
 */
public class Jedis_ {

    @Test
    public void con(){
        Jedis jedis = new Jedis("192.168.198.135", 6379);

        //如果配置了密码，需要身份校验
        jedis.auth("foobared");

        String res = jedis.ping();
        System.out.println("连接成功返回结果：" + res);
        jedis.close();
    }

    @Test
    public void key(){
        Jedis jedis = new Jedis("192.168.198.135", 6379);
        String foobared = jedis.auth("foobared");
        jedis.set("k1","v1");
        jedis.set("k2","v2");
        jedis.set("k2","v2");

        for (String key : jedis.keys("*")){
            System.out.println("key==>" + key);
        }
        System.out.println(jedis.ttl("k1"));
        System.out.println(jedis.get("k2"));
        System.out.println(jedis.exists("k1"));
        jedis.close();
    }

    @Test
    public void string(){
        Jedis jedis = new Jedis("192.168.198.135", 6379);
        String foobared = jedis.auth("foobared");
        jedis.mset("k1","jack","k2","scott","k3","zh");
        List<String> mget = jedis.mget("k1", "k2", "k3");
        for (String s : mget) {
            System.out.println("s--" + s);
        }
        jedis.close();
    }

    @Test
    public void  list(){
        Jedis jedis = new Jedis("192.168.198.135", 6379);
        jedis.auth("foobared");

        jedis.lpush("name_list","jack","scott","tom");
        List<String> nameList = jedis.lrange("name_list", 0, -1);
        for (String s : nameList) {
            System.out.println("name--" + s);
        }
        jedis.close();
    }

    @Test
    public void set(){
        Jedis jedis = new Jedis("192.168.198.135", 6379);
        jedis.auth("foobared");

        jedis.sadd("city","北京","深圳");
        jedis.sadd("city","广州");

        Set<String> cities = jedis.smembers("city");
        for (String city : cities) {
            System.out.println("city--" + city);
        }
        jedis.close();
    }

    @Test
    public void hash(){
        Jedis jedis = new Jedis("192.168.198.135", 6379);
        String foobared = jedis.auth("foobared");

        jedis.hset("hash01", "name", "李白");
        Map<String, String> map = new HashMap<>();
        map.put("age","20");
        map.put("sex","male");
        jedis.hmset("hash01",map);

        List<String> persons = jedis.hmget("hash01", "name", "age", "sex");
        for (String person : persons) {
            System.out.println("person--" + person);
        }

        jedis.close();

    }

    @Test
    public void zset() {
        Jedis jedis = new Jedis("x.x.x.x", 6379);
        jedis.auth("password");

        jedis.zadd("hero", 1, "关羽");
        jedis.zadd("hero", 2, "张飞");
        jedis.zadd("hero", 3, "赵云");
        jedis.zadd("hero", 4, "马超");
        jedis.zadd("hero", 5, "黄忠");

        Set<String> heros = jedis.zrevrange("hero", 0, 1);
        for (String hero : heros) {
            System.out.println("hero=" + hero);
        }
        jedis.close();
    }
}
