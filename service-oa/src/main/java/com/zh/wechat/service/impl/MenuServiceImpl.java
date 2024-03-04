package com.zh.wechat.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zh.model.wechat.Menu;
import com.zh.vo.wechat.MenuVo;
import com.zh.wechat.mapper.MenuMapper;
import com.zh.wechat.service.MenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单 服务实现类
 * </p>
 *
 * @author zh
 * @since 2023-12-12
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Resource
    private WxMpService wxMpService;

    //获取全部菜单
    @Override
    public List<MenuVo> findMenuInfo() {
        ArrayList<MenuVo> list = new ArrayList<>();

        //1.查询出所有菜单的list集合
        List<Menu> menuList = baseMapper.selectList(null);

        //2.查询所有菜单中的一级菜单
        List<Menu> oneMenuList = menuList.stream().filter(menu -> menu.getParentId().longValue() == 0).collect(Collectors.toList());

        //3.遍历一级菜单list集合
        for (Menu oneMenu : oneMenuList) {
            //以及菜单Menu类型转换为MenuVo类型
            MenuVo oneMenuVo = new MenuVo();
            BeanUtils.copyProperties(oneMenu,oneMenuVo);

            //4.获取每个一级菜单里面所有的二级菜单id 和 parent_id比较
            List<Menu> twoMenuList = menuList.stream()
                                    .filter(menu -> menu.getParentId().longValue() == oneMenuVo.getId())
                                    .collect(Collectors.toList());

            //5.把一级菜单里面所有的二级菜单获取到，封装到以及菜单children集合中
            //List<Menu> -> List<MenuVo>
            ArrayList<MenuVo> children = new ArrayList<>();
            for (Menu twoMenu : twoMenuList) {
                MenuVo twoMenuVo = new MenuVo();
                BeanUtils.copyProperties(twoMenu,twoMenuVo);
                children.add(twoMenuVo);
            }
            oneMenuVo.setChildren(children);

            //把每个封装好的菜单放到最终list集合
            list.add(oneMenuVo);
        }

        return list;
    }

    //同步菜单
    @Override
    public void syncMenu() {
        //1.查询菜单数据，封装成微信要求的菜单格式
        List<MenuVo> menuVoList = this.findMenuInfo();

        //菜单
        JSONArray buttonList = new JSONArray();
        for (MenuVo oneMenuVo : menuVoList) {
            JSONObject one = new JSONObject();
            one.put("name",oneMenuVo.getName());
            if (CollectionUtils.isEmpty(oneMenuVo.getChildren())){
                one.put("type",oneMenuVo.getType());
                one.put("url","http://oa.zh.cn/#" + oneMenuVo.getUrl());
            }else{
                JSONArray subButton = new JSONArray();
                for (MenuVo twoMenuVo : oneMenuVo.getChildren()) {
                    JSONObject view = new JSONObject();
                    view.put("type",twoMenuVo.getType());
                    if (twoMenuVo.getType().equals("view")){
                        view.put("name",twoMenuVo.getName());
                        //H5页面地址
                        view.put("url","http://oa.zh.cn/#" + twoMenuVo.getUrl());
                    }else{
                        view.put("name", twoMenuVo.getName());
                        view.put("key", twoMenuVo.getMeunKey());
                    }
                    subButton.add(view);
                }
                one.put("sub_button", subButton);
            }
            buttonList.add(one);
        }
        JSONObject button = new JSONObject();
        button.put("button", buttonList);

        //2.调用工具方法，完成菜单推送
        try {
            wxMpService.getMenuService().menuCreate(button.toJSONString());
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeMenu() {
        try {
            wxMpService.getMenuService().menuDelete();
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }
}
