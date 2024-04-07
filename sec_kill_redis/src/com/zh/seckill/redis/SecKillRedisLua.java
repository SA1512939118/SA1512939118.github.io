package com.zh.seckill.redis;

import com.zh.seckill.util.JedisPoolUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author zh
 * @version 1.0
 * @date 2024/4/7 1:19
 * 使用Lua脚本完成秒杀
 */
public class SecKillRedisLua {

    static String secKillScript = "local userid=KEYS[1];\r\n" +
            "local ticketno=KEYS[2];\r\n" +
            "local stockKey='sk:'..ticketno..\":ticket\";\r\n" +
            "local usersKey='sk:'..ticketno..\":user\";\r\n" +
            "local userExists=redis.call(\"sismember\",usersKey,userid);\r\n" +
            "if tonumber(userExists)==1 then \r\n" +
            " return 2;\r\n" +
            "end\r\n" +
            "local num= redis.call(\"get\" ,stockKey);\r\n" +
            "if tonumber(num)<=0 then \r\n" +
            " return 0;\r\n" +
            "else \r\n" +
            " redis.call(\"decr\",stockKey);\r\n" +
            " redis.call(\"sadd\",usersKey,userid);\r\n" +
            "end\r\n" +
            "return 1";

    public static boolean doSecKill(String uid, String ticketNo){
        JedisPool jedisPool = JedisPoolUtil.getJedisPoolInstance();
        Jedis jedis = jedisPool.getResource();

        //加载Lua脚本，返回一个校验码
        String sha1 = jedis.scriptLoad(secKillScript);

        //执行lua脚本，传入校验码和参数
        Object result = jedis.evalsha(sha1, 2, uid, ticketNo);
        String resString = String.valueOf(result);

        if ("0".equals(result)){
            System.out.println("售罄");
            jedis.close();
            return false;
        }
        if ("2".equals(result)){
            System.out.println("请勿重复购买");
            jedis.close();
            return false;
        }
        if ("1".equals(result)){
            System.out.println("抢购成功");
            jedis.close();
            return true;
        }else {
            System.out.println("购票失败");
            jedis.close();
            return false;
        }
    }
}
