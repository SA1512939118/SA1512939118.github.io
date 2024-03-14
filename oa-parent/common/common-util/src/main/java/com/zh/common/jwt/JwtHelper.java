package com.zh.common.jwt;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @author zh
 * @version 1.0
 * @date 2023/11/17 14:32
 */

//jwt工具类
public class JwtHelper {
    //token有效时间
    private static long tokenExpiration = 365 * 24 * 60 * 60 * 1000;
    //秘钥，根据他进行数据编码加密
    private static String tokenSignKey = "123456";

    //根据用户id和用户名称生成token字符串
    public static String createToken(Long userId, String username) {
        String token = Jwts.builder()
                //分类
                .setSubject("AUTH-USER")

                //设置过期时间：当前时间+有效时长
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))

                //设置主题部分
                .claim("userId", userId)
                .claim("username", username)

                //签名部分，编码加密
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }


    //从token中，根据秘钥获取用户id
    public static Long getUserId(String token) {
        try {
            if (StringUtils.isEmpty(token)) return null;

            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            Integer userId = (Integer) claims.get("userId");
            return userId.longValue();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //从token中，根据秘钥获取用户名称
    public static String getUsername(String token) {
        try {
            if (StringUtils.isEmpty(token)) return "";

            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            return (String) claims.get("username");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String token = JwtHelper.createToken(7L, "zhangsan");
        System.out.println("token = " + token);
        System.out.println("JwtHelper.getUserId(token) = " + JwtHelper.getUserId(token));
        System.out.println("JwtHelper.getUsername(token) = " + JwtHelper.getUsername(token));
    }

}
