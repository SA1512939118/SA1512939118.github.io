package com.zh.auth;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zh.auth.mapper.SysRoleMapper;
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
public class TestMpDemo1 {

    @Resource
    private SysRoleMapper sysRoleMapper;
    @Test
    public void getAll(){
        List<SysRole> list =
                sysRoleMapper.selectList(null);
        System.out.println(list);
    }

    @Test
    public void insertAdd(){
        SysRole role = new SysRole();
        role.setRoleName("角色管理员1");
        role.setRoleCode("role");
        role.setDescription("角色管理员");
        int rows = sysRoleMapper.insert(role);
        System.out.println("insert = " + rows);
        System.out.println("role.id = " + role.getId());
    }

    @Test
    public void update(){
        SysRole sysRole = sysRoleMapper.selectById(10);
        sysRole.setRoleName("oa角色管理员");
        int rows = sysRoleMapper.updateById(sysRole);
        System.out.println("rows = " + rows);
    }

    @Test
    public void deleteId(){
        int rows = sysRoleMapper.deleteById(10);
    }

    @Test
    public void deleteBatches(){
        int result = sysRoleMapper.deleteBatchIds(Arrays.asList(1,2));
    }

    @Test
    public void testQuery1(){
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.eq("role_name","总经理");
        List<SysRole> list = sysRoleMapper.selectList(wrapper);
        System.out.println("list = " + list);
    }

    @Test
    public void testQuery2(){
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleName,"总经理");
        List<SysRole> list = sysRoleMapper.selectList(wrapper);
        System.out.println("list = " + list);
    }
}
