package com.zh.auth;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zh.auth.mapper.SysRoleMapper;
import com.zh.auth.service.SysRoleService;
import com.zh.model.system.SysRole;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author zh
 * @version 1.0
 * @date 2023/11/11 19:49
 */
@SpringBootTest
public class TestMpDemo2 {

    @Resource
    private SysRoleService service;
    @Test
    public void getAll(){
        List<SysRole> list = service.list();
        System.out.println(list);
    }


}
