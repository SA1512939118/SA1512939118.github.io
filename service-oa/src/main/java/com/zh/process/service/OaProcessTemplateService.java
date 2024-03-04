package com.zh.process.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zh.model.process.ProcessTemplate;

/**
 * <p>
 * 审批模板 服务类
 * </p>
 *
 * @author zh
 * @since 2023-11-30
 */
public interface OaProcessTemplateService extends IService<ProcessTemplate> {
    //分页查询审批模板，把审批类型对应名称查询到，并封装进ProcessTemplate中的属性中去
    IPage<ProcessTemplate> selectPageProcessTemplate(Page<ProcessTemplate> pageParam);

    //部署流程定义（发布）
    void publish(Long id);
}
