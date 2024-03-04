package com.zh.wechat.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zh.model.wechat.Menu;
import com.zh.vo.wechat.MenuVo;

import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author zh
 * @since 2023-12-12
 */
public interface MenuService extends IService<Menu> {

    //获取全部菜单
    List<MenuVo> findMenuInfo();

    //同步菜单
    void syncMenu();

    //删除菜单
    void removeMenu();
}
