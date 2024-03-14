package com.zh.process.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zh.model.process.Process;
import com.zh.vo.process.ApprovalVo;
import com.zh.vo.process.ProcessFormVo;
import com.zh.vo.process.ProcessQueryVo;
import com.zh.vo.process.ProcessVo;

import java.util.Map;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author zh
 * @since 2023-12-04
 */
public interface OaProcessService extends IService<Process> {

    //审批管理列表
    IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo);
    void deployByZip(String deployPath);

    //启动流程实例
    void startUp(ProcessFormVo processFormVo);

    //查询待处理任务列表
    IPage<ProcessVo> findPending(Page<Process> pageParam);

    //查看审批详细信息
    Map<String, Object> show(Long id);

    //审批
    void approve(ApprovalVo approvalVo);

    //查询已处理任务
    IPage<ProcessVo> findProcessed(Page<Process> pageParam);

    //查询已发起的信息
    IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam);
}
