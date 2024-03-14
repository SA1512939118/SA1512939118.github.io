package com.zh.auth.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zh.model.system.SysMenu;
import com.zh.vo.system.AssignMenuVo;
import com.zh.vo.system.RouterVo;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author zh
 * @since 2023-11-16
 */
public interface SysMenuService extends IService<SysMenu> {

    //菜单列表接口
    List<SysMenu> findNodes();

    //删除菜单
    void removeMenuById(Long id);

    List<SysMenu> findMenuByRoleId(Long roleId);

    void doAssign(AssignMenuVo assignMenuVo);

    //根据用户id获取用户可以操作的菜单
    List<RouterVo> findUserMenuListByUserId(Long userId);

    //根据用户id获取用户可以操作菜单列表
    List<String> findUserPermsByUserId(Long userId);
}
