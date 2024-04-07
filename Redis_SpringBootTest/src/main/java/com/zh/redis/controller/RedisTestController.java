package com.zh.redis.controller;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zh
 * @version 1.0
 * @date 2024/3/27 20:33
 */
@RestController
@RequestMapping("/redisTest")
public class RedisTestController {

    @Resource
    private RedisTemplate redisTemplate;
    @GetMapping("/t1")
    public String T1(){
        redisTemplate.opsForValue().set("book","天龙八部");

        String book = (String) redisTemplate.opsForValue().get("book");
        return book;
    }

    //操作list，hash，set，zset
    @GetMapping("/t2")
    public String t1(){
        redisTemplate.opsForList().leftPush("books","笑傲江湖");
        redisTemplate.opsForList().leftPush("books","hello,java");

        List books = redisTemplate.opsForList().range("books", 0, -1);
        String bookList = "";
        for (Object book : books) {
            System.out.println("book = " + book.toString());
            bookList += book.toString() + " ";
        }
        return bookList;
    }
}
