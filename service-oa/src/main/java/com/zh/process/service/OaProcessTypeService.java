package com.zh.process.service;

import com.zh.model.process.ProcessType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author zh
 * @since 2023-11-30
 */
public interface OaProcessTypeService extends IService<ProcessType> {

    //查询所有审批分类和每个分类所有的审批模板
    List<ProcessType> findProcessType();
}
