package com.zh.auth.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.auth.service.SysUserService;
import com.zh.common.result.Result;
import com.zh.common.utils.MD5;
import com.zh.model.system.SysUser;
import com.zh.vo.system.SysUserQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author zh
 * @since 2023-11-15
 */
@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/admin/system/sysUser")
public class SysUserController {

    @Resource
    private SysUserService service;

    @ApiOperation(value = "更新状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Long id,
                               @PathVariable Integer status){
        service.updateStatus(id,status);
        return Result.ok();
    }

    //用户的条件分页查询
    @ApiOperation("用户的条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page,
                        @PathVariable Long limit,
                        SysUserQueryVo sysUserQueryVo){

        Page<SysUser> pageParam = new Page<>(page,limit);

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        String username = sysUserQueryVo.getKeyword();
        String createTimeBegin = sysUserQueryVo.getCreateTimeBegin();
        String createTimeEnd = sysUserQueryVo.getCreateTimeEnd();

        if (!StringUtils.isEmpty(username)){
            wrapper.like(SysUser::getUsername,username);
        }
        if (!StringUtils.isEmpty(createTimeBegin)){
            wrapper.ge(SysUser::getCreateTime,createTimeBegin);
        }
        if (!StringUtils.isEmpty(createTimeEnd)){
            wrapper.le(SysUser::getCreateTime,createTimeEnd);
        }

        IPage<SysUser> pageModel = service.page(pageParam, wrapper);
        return Result.ok(pageModel);

    }

    @ApiOperation("获取用户")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id){
        SysUser user = service.getById(id);
        return Result.ok(user);
    }

    @ApiOperation("保存用户")
    @PostMapping("save")
    public Result save(@RequestBody SysUser user){
        String passwordMD5 = MD5.encrypt(user.getPassword());
        user.setPassword(passwordMD5);

        service.save(user);
        return Result.ok();
    }

    @ApiOperation("更新用户")
    @PutMapping("update")
    public Result updateById(@RequestBody SysUser user){
        service.updateById(user);
        return Result.ok();
    }


    @ApiOperation("删除用户")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id){
        service.removeById(id);
        return Result.ok();
    }
}

