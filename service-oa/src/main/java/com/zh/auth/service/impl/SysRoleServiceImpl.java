package com.zh.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zh.auth.mapper.SysRoleMapper;
import com.zh.auth.service.SysRoleService;
import com.zh.auth.service.SysUserRoleService;
import com.zh.model.system.SysRole;
import com.zh.model.system.SysUserRole;
import com.zh.vo.system.AssginRoleVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zh
 * @version 1.0
 * @date 2023/11/11 21:22
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Resource
    private SysUserRoleService sysUserRoleService;


    //1.查询所有角色 和 当前用户所属角色
    @Override
    public Map<String, Object> findRoleDataByUserId(Long userId) {
        //1.查询所有角色，返回list集合
        List<SysRole> allRoleList =
                baseMapper.selectList(null);

        //2.根据userid查询角色用户关系表，查询userid对应所有角色id
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId,userId);
        List<SysUserRole> existUserRoleList = sysUserRoleService.list(wrapper);

        //从角色list集合中，遍历出所有角色id
        List<Long> existRoleIdList =
                existUserRoleList.stream().map(c -> c.getRoleId()).collect(Collectors.toList());

        //3.查询所有角色id，找到对应角色信息
        List<SysRole> assignRoleList = new ArrayList<>();
        for(SysRole sysRole : allRoleList){
            if (existRoleIdList.contains(sysRole.getId())){
                assignRoleList.add(sysRole);
            }
        }
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("assginRoleList",assignRoleList);
        roleMap.put("allRoleList",allRoleList);
        return roleMap;
    }

    //为用户分配角色
    @Override
    public void doAssign(AssginRoleVo assginRoleVo) {
        //把用户之前分配角色删除，用户角色关系表里根据userid删除
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId,assginRoleVo.getUserId());
        sysUserRoleService.remove(wrapper);

        //重新进行分配
        List<Long> roleIdList = assginRoleVo.getRoleIdList();
        for(Long roleId : roleIdList){
            if (StringUtils.isEmpty(roleId)){
                continue;
            }
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(assginRoleVo.getUserId());
            sysUserRole.setRoleId(roleId);
            sysUserRoleService.save(sysUserRole);
        }
    }
}
