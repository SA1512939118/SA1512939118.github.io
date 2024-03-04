package com.zh.process.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.model.process.ProcessTemplate;
import com.zh.model.process.ProcessType;
import com.zh.process.mapper.OaProcessTemplateMapper;
import com.zh.process.service.OaProcessService;
import com.zh.process.service.OaProcessTemplateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zh.process.service.OaProcessTypeService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 审批模板 服务实现类
 * </p>
 *
 * @author zh
 * @since 2023-11-30
 */
@Service
public class OaProcessTemplateServiceImpl extends ServiceImpl<OaProcessTemplateMapper, ProcessTemplate> implements OaProcessTemplateService {

    @Resource
    private OaProcessTypeService processTypeService;

    @Resource
    private OaProcessService processService;

    @Override
    public IPage<ProcessTemplate> selectPageProcessTemplate(Page<ProcessTemplate> pageParam) {
        //1.调用mapper里面的方法实现分页查询
        Page<ProcessTemplate> processTemplatePage = baseMapper.selectPage(pageParam, null);
        //2.从第一步返回的分页数据中，获取列表list集合
        List<ProcessTemplate> processTemplateList = processTemplatePage.getRecords();
        //3.遍历list集合，得到每个对象的审批类型id,拿着id去数据库查询
        for (ProcessTemplate processTemplate : processTemplateList) {
            Long processTypeId = processTemplate.getProcessTypeId();
            LambdaQueryWrapper<ProcessType> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProcessType::getId, processTypeId);
            ProcessType processType = processTypeService.getOne(wrapper);
            if (processType == null) {
                continue;
            }
            //4.完成最终封装processTypeName
            processTemplate.setProcessTypeName(processType.getName());
        }

        return processTemplatePage;
    }

    @Override
    public void publish(Long id) {
        //修改模板发布状态，1表示已发布
        ProcessTemplate processTemplate = baseMapper.selectById(id);
        processTemplate.setStatus(1);
        baseMapper.updateById(processTemplate);

        //流程定义部署
        if (!StringUtils.isEmpty(processTemplate.getProcessDefinitionPath())){
            processService.deployByZip(processTemplate.getProcessDefinitionPath());
        }
    }
}
