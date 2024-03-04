package com.zh.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zh.auth.service.SysMenuService;
import com.zh.auth.service.SysUserService;
import com.zh.common.config.exception.ZhException;
import com.zh.common.jwt.JwtHelper;
import com.zh.common.result.Result;
import com.zh.common.utils.MD5;
import com.zh.model.system.SysUser;
import com.zh.vo.system.LoginVo;
import com.zh.vo.system.RouterVo;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zh
 * @version 1.0
 * @date 2023/11/13 17:15
 */
@Api(tags = "后台登录管理")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private SysMenuService sysMenuService;

    //Login
    @PostMapping("login")
    public Result loginIn(@RequestBody LoginVo loginVo){
//        HashMap<String, String> map = new HashMap<>();
//        map.put("token","admin-token");
//        System.out.println("login被调用");
//        return Result.ok(map);

        //1.获取输入的用户名和密码
        //2.根据用户查看数据库
        String username = loginVo.getUsername();
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername,username);
        SysUser sysUser = sysUserService.getOne(wrapper);


        //3.用户信息是否存在
        if (sysUser == null){
            throw new ZhException(201,"用户不存在");
        }

        //4.判断密码是否存在
        String password_db = sysUser.getPassword();
        String password_input = MD5.encrypt(loginVo.getPassword());//将登录界面接收到的密码进行加密后与数据库查询到的相比较
        if (!password_db.equals(password_input)){
            throw new ZhException(201,"密码错误");
        }

        //5.判断用户是否被禁用
        if (sysUser.getStatus().intValue() == 0){
            throw new ZhException(201,"用户已经被禁用");
        }
        //6.使用jwt根据用户id和名称生成token字符串
        String token
                = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());

        //7.返回
        HashMap<Object, Object> map = new HashMap<>();
        map.put("token",token);
        return Result.ok(map);
    }
    //info
    @GetMapping("info")
    public Result info(HttpServletRequest request) {
        //1.从请求头里面获取用户信息（获取请求头token字符串）
        String token = request.getHeader("token");

        //2. 从token字符串中获取用户id和名称
        Long userId = JwtHelper.getUserId(token);

        //3.根据用户id查询数据库，获取用户信息
        SysUser sysUser = sysUserService.getById(userId);

        //4.根据用户id获取用户可以操作的菜单
        //查询数据库动态构建路由结构，进行显示
        List<RouterVo> routerList = sysMenuService.findUserMenuListByUserId(userId);


        //5.根据用户id获取用户可以操作菜单列表
        List<String> permsList = sysMenuService.findUserPermsByUserId(userId);

        //6.返回相应数据
        Map<String, Object> map = new HashMap<>();
        map.put("roles","[admin]");
        map.put("name",sysUser.getName());
        map.put("avatar","https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
        System.out.println("info被调用");
        //TODO 返回用户可以操作菜单
        map.put("routers",routerList);

        //TODO 返回用户可以操作按钮
        map.put("buttons",permsList);
        return Result.ok(map);
    }

    @PostMapping("logout")
    public Result logout(){
        System.out.println("logout被调用");
        return Result.ok();
    }
}
