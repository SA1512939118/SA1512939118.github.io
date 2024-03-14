package com.zh.process.service.impl;

import com.zh.auth.service.SysUserService;
import com.zh.model.process.ProcessRecord;
import com.zh.model.system.SysUser;
import com.zh.process.mapper.OaProcessRecordMapper;
import com.zh.process.service.OaProcessRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zh.security.custom.LoginUserInfoHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 审批记录 服务实现类
 * </p>
 *
 * @author zh
 * @since 2023-12-08
 */
@Service
public class OaProcessRecordServiceImpl extends ServiceImpl<OaProcessRecordMapper, ProcessRecord> implements OaProcessRecordService {


    @Resource
    private SysUserService sysUserService;
    @Override
    public void record(Long processId, Integer status, String description) {

        Long userId = LoginUserInfoHelper.getUserId();
        SysUser sysUser = sysUserService.getById(userId);

        ProcessRecord processRecord = new ProcessRecord();
        processRecord.setProcessId(processId);
        processRecord.setStatus(status);
        processRecord.setDescription(description);
        processRecord.setOperateUserId(userId);
        processRecord.setOperateUser(sysUser.getName());
        baseMapper.insert(processRecord);
    }
}
