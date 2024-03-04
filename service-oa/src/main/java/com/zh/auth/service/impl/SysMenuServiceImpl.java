package com.zh.auth.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zh.auth.mapper.SysMenuMapper;
import com.zh.auth.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zh.auth.service.SysRoleMenuService;
import com.zh.auth.utils.MenuHelper;
import com.zh.common.config.exception.ZhException;
import com.zh.model.system.SysMenu;
import com.zh.model.system.SysRoleMenu;
import com.zh.vo.system.AssignMenuVo;
import com.zh.vo.system.MetaVo;
import com.zh.vo.system.RouterVo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author zh
 * @since 2023-11-16
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Resource
    private SysRoleMenuService sysRoleMenuService;
    @Override
    public List<SysMenu> findNodes() {

        //1.查询所有菜单数据
        List<SysMenu> sysMenuList = baseMapper.selectList(null);
        //2.构建树形结构
        List<SysMenu> resultList = MenuHelper.buildTree(sysMenuList);
        return resultList;
    }

    @Override
    public void removeMenuById(Long id) {
        //判断当前菜单是否有下一层菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId,id);
        Integer count = baseMapper.selectCount(wrapper);
        if (count > 0){
            throw new ZhException(201,"菜单不能删除");
        }
        baseMapper.deleteById(id);
    }

    //查询所有菜单 和 角色分配的菜单
    @Override
    public List<SysMenu> findMenuByRoleId(Long roleId) {
        //1.查询所有菜单-添加条件status=1，表示菜单可用
        LambdaQueryWrapper<SysMenu> wrapperSysMenu = new LambdaQueryWrapper<>();
        wrapperSysMenu.eq(SysMenu::getStatus, 1);
        List<SysMenu> allSysMenuList = baseMapper.selectList(wrapperSysMenu);

        //2.根据角色id查询 角色菜单关系表里面 角色id对应的所有菜单id
        LambdaQueryWrapper<SysRoleMenu> wrapperSysRoleMenu = new LambdaQueryWrapper<>();
        wrapperSysRoleMenu.eq(SysRoleMenu::getRoleId,roleId);
        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuService.list(wrapperSysRoleMenu);

        //获取角色所有菜单的list集合
        List<Long> menuList = sysRoleMenuList.stream().map(c -> c.getMenuId()).collect(Collectors.toList());

        //3.拿着角色所有菜单id，遍历所有菜单，获取对应菜单对象
        allSysMenuList.stream().forEach(item -> {
            if (menuList.contains(item.getId())){
                item.setSelect(true);
            }else{
                item.setSelect(false);
            }
        });
        //4.返回规定格式的菜单列表
        List<SysMenu> sysMenuList = MenuHelper.buildTree(allSysMenuList);

        return sysMenuList;
    }

    //给角色分配菜单
    @Override
    public void doAssign(AssignMenuVo assignMenuVo) {
        //1.根据角色id 删除菜单角色关系表中的 已分配菜单数据
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId,assignMenuVo.getRoleId());
        sysRoleMenuService.remove(wrapper);

        //2.从参数assignMenuVo获得角色新分配菜单id列表，遍历，把每个菜单角色添加到菜单角色关系表中去
        List<Long> menuIdList = assignMenuVo.getMenuIdList();
        for (Long menuId : menuIdList) {
            if (StringUtils.isEmpty(menuId)) {
                continue;
            }
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(assignMenuVo.getRoleId());
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenuService.save(sysRoleMenu);
        }
    }

    //根据用户id获取用户可以操作的菜单
    @Override
    public List<RouterVo> findUserMenuListByUserId(Long userId) {
        List<SysMenu> sysMenuList = null;
        //1.判断当前用户是不是管理员 userId = 1是管理员

        if (userId == 1){
            //1.1如果是管理员，查询所有菜单列表
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus,1);
            wrapper.orderByAsc(SysMenu::getSortValue);
            sysMenuList = baseMapper.selectList(wrapper);
        }else{
            //1.2如果不是管理员，根据userId查询可以操作的菜单列表
            //多表关联查询：用户角色关系表、角色菜单关系表、菜单表
            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }

        //2.把查询出来的数据了列表构建成框架要求的路由数据结构
        //使用菜单操作工具类构建树形结构
        List<SysMenu> sysMenuTreeList = MenuHelper.buildTree(sysMenuList);
        //构建成框架要求的路由结构
        List<RouterVo> routerList = this.buildRouter(sysMenuTreeList);
        return routerList;
    }

    //构建成框架要求的路由结构
    private List<RouterVo> buildRouter(List<SysMenu> menus) {
        //创建list集合，存储最终数据
        ArrayList<RouterVo> routers = new ArrayList<>();

        //menus遍历，得到每个菜单对象
        for (SysMenu menu : menus) {
            RouterVo router = new RouterVo();
            router.setHidden(false);
            router.setAlwaysShow(false);
            router.setPath(getRouterPath(menu));
            router.setComponent(menu.getComponent());
            router.setMeta(new MetaVo(menu.getName(),menu.getIcon()));
            //下一层数据部分
            List<SysMenu> children = menu.getChildren();
            if (menu.getType().intValue() == 1){//表结构中，type字段：1表示这是个菜单，2表示这是个按键
                //加载出来下面隐藏路由
                List<SysMenu> hiddenMenuList = children.stream()
                        .filter(item -> !StringUtils.isEmpty(item.getPerms()))
                        .collect(Collectors.toList());
                for (SysMenu hiddenMenu : hiddenMenuList) {
                    RouterVo hiddenRouter = new RouterVo();
                    hiddenRouter.setHidden(true);
                    hiddenRouter.setAlwaysShow(false);
                    hiddenRouter.setPath(getRouterPath(hiddenMenu));
                    hiddenRouter.setComponent(hiddenMenu.getComponent());
                    hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(),hiddenMenu.getIcon()));

                    routers.add(hiddenRouter);
                }
            }else {
                if (!CollectionUtils.isEmpty(children)){
                    if (children.size() > 0){
                        router.setAlwaysShow(true);
                    }
                    //递归构建子菜单结构
                    router.setChildren(buildRouter(children));
                }
            }
            routers.add(router);
        }
        return routers;
    }



    //根据用户id获取用户可以操作按钮列表
    @Override
    public List<String> findUserPermsByUserId(Long userId) {
        List<SysMenu> sysMenuList = null;
        //1如果是管理员，查询所有菜单列表
        if (userId.longValue() == 1){
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus,1);
            sysMenuList = baseMapper.selectList(wrapper);
        }else{
            //2如果不是管理员，根据userId查询可以操作的按钮列表
            //多表关联查询：用户角色关系表、角色菜单关系表、菜单表
            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }
        //3从查询出来的数据里面，获取可以操作按钮值的list集合，返回
        List<String> permsList = sysMenuList.stream()
                .filter(item -> item.getType() == 2)
                .map(item -> item.getPerms())
                .collect(Collectors.toList());
        return permsList;
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = "/" + menu.getPath();
        if(menu.getParentId().intValue() != 0) {
            routerPath = menu.getPath();
        }
        return routerPath;
    }
}
