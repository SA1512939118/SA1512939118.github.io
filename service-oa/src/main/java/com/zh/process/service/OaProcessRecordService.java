package com.zh.process.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zh.model.process.ProcessRecord;

/**
 * <p>
 * 审批记录 服务类
 * </p>
 *
 * @author zh
 * @since 2023-12-08
 */
public interface OaProcessRecordService extends IService<ProcessRecord> {

    void record(Long processId,Integer status,String description);
}
