package com.zh.auth.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zh.model.system.SysUser;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author zh
 * @since 2023-11-15
 */
public interface SysUserService extends IService<SysUser> {

    void updateStatus(Long id, Integer status);

    //根据用户名进行查询
    SysUser getUserByUsername(String username);

    //查询当前用户信息
    Map<String, Object> getCurrentUser();
}
