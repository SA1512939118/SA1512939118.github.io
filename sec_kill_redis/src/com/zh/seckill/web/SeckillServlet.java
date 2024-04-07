package com.zh.seckill.web; /**
 * @author zh
 * @date 2024/4/6 23:10
 * @version 1.0
 */

import com.zh.seckill.redis.SecKillRedis;
import com.zh.seckill.redis.SecKillRedisLua;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Random;

public class SeckillServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.请求时，模拟生成一个userId
        String userId = new Random().nextInt(10000) + "";
        String ticketNo = request.getParameter("ticketNo");

        //boolean isOk = SecKillRedis.doSecKill(userId, ticketNo);
        //调用Lua脚本进行秒杀
        boolean isOk = SecKillRedisLua.doSecKill(userId, ticketNo);
        //2.将结果返回给前端
        response.getWriter().print(isOk);
    }
}
