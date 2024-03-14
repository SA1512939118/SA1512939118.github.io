package com.zh.auth.utils;

import com.zh.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zh
 * @version 1.0
 * @date 2023/11/16 10:31
 */
public class MenuHelper {
    //递归方式递归原则
    public static List<SysMenu> buildTree(List<SysMenu> sysMenuList) {
        ArrayList<SysMenu> trees = new ArrayList<>();
        //先找入口
        for (SysMenu sysMenu : sysMenuList) {
            if (sysMenu.getParentId().longValue() == 0){
                trees.add(getChildren(sysMenu,sysMenuList));//进入递归
            }
        }

        return trees;
    }

    //递归获取子菜单
    private static SysMenu getChildren(SysMenu sysMenu, List<SysMenu> sysMenuList) {
        sysMenu.setChildren(new ArrayList<>());

        //遍历list，找到其中parentId与sysMenu的id相同的
        for (SysMenu it : sysMenuList) {
            if (it.getParentId().longValue() == sysMenu.getId().longValue()){
                if(sysMenu.getChildren() == null){
                    sysMenu.setChildren(new ArrayList<>());
                }
                sysMenu.getChildren().add(getChildren(it,sysMenuList));
            }
        }
        return sysMenu;

    }
}
