package com.zh.wechat.controller;


import com.zh.common.result.Result;
import com.zh.model.wechat.Menu;
import com.zh.vo.wechat.MenuVo;
import com.zh.wechat.service.MenuService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 菜单 前端控制器
 * </p>
 *
 * @author zh
 * @since 2023-12-12
 */
@RestController
@RequestMapping("/admin/wechat/menu")
public class MenuController {

    @Resource
    private MenuService menuService;

    @ApiOperation(value = "删除菜单")
    @DeleteMapping("removeMenu")
    public Result removeMenu(){
        menuService.removeMenu();
        return Result.ok();
    }

    @ApiOperation(value = "同步菜单")
    @GetMapping("syncMenu")
    public Result createMenu(){
        menuService.syncMenu();
        return Result.ok();
    }

    @ApiOperation(value = "获取全部菜单")
    @GetMapping("findMenuInfo")
    public Result findMenuInfo(){
        List<MenuVo> menuList = menuService.findMenuInfo();
        return Result.ok(menuList);
    }

    @ApiOperation("获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id){
        Menu menu = menuService.getById(id);
        return Result.ok(menu);
    }

    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody Menu menu){
        menuService.save(menu);
        return Result.ok();
    }

    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result update(@RequestBody Menu menu){
        menuService.updateById(menu);
        return Result.ok();
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id){
        menuService.removeById(id);
        return Result.ok();
    }

}

