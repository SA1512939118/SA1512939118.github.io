package com.zh.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zh.model.system.SysRole;
import com.zh.vo.system.AssginRoleVo;

import java.util.Map;
import java.util.Objects;

/**
 * @author zh
 * @version 1.0
 * @date 2023/11/11 21:21
 */
public interface SysRoleService extends IService<SysRole> {
    Map<String, Object> findRoleDataByUserId(Long userId);

    void doAssign(AssginRoleVo assginRoleVo);
}
