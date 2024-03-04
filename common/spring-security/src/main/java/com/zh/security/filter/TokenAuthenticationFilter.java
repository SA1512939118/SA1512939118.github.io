package com.zh.security.filter;

import com.alibaba.fastjson.JSON;
import com.zh.common.jwt.JwtHelper;
import com.zh.common.result.ResponseUtil;
import com.zh.common.result.Result;
import com.zh.common.result.ResultCodeEnum;
import com.zh.security.custom.LoginUserInfoHelper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author zh
 * @version 1.0
 * @date 2023/11/26 13:35
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private RedisTemplate redisTemplate;
    public TokenAuthenticationFilter(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        //如果是登录本身接口，直接放行
        if ("/admin/system/index/login".equals(request.getRequestURI())){
            chain.doFilter(request,response);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if (null != authentication){//放到上下文中
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request,response);
        }else{
            ResponseUtil.out(response, Result.build(null, ResultCodeEnum.LOGIN_ERROR));
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        //请求头是否有token
        String token = request.getHeader("token");
        if (!StringUtils.isEmpty(token)){
            String username = JwtHelper.getUsername(token);
            if (!StringUtils.isEmpty(username)){
                //通过ThreadLocal记录当前用户信息
                LoginUserInfoHelper.setUserId(JwtHelper.getUserId(token));
                LoginUserInfoHelper.setUsername(username);
                String authString = (String)redisTemplate.opsForValue().get(username);
                List<Map> mapList = JSON.parseArray(authString, Map.class);
                System.out.println(mapList);
                List<SimpleGrantedAuthority> authList = new ArrayList<>();
                for (Map map : mapList) {
                   String authority =  (String)map.get("authority");
                   authList.add(new SimpleGrantedAuthority(authority));
                }
                return new UsernamePasswordAuthenticationToken(username,null, authList);
            }else {
                return new UsernamePasswordAuthenticationToken(username,null, new ArrayList<>());
            }
        }
        return null;
    }
}
