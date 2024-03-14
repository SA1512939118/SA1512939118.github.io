package com.zh.process.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zh.model.process.ProcessTemplate;
import com.zh.model.process.ProcessType;
import com.zh.process.mapper.OaProcessTypeMapper;
import com.zh.process.service.OaProcessTemplateService;
import com.zh.process.service.OaProcessTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author zh
 * @since 2023-11-30
 */
@Service
public class OaProcessTypeServiceImpl extends ServiceImpl<OaProcessTypeMapper, ProcessType> implements OaProcessTypeService {

    @Resource
    private OaProcessTemplateService processTemplateService;

    //查询所有审批分类和每个分类所有的审批模板
    @Override
    public List<ProcessType> findProcessType() {
        //查询所有审批分类
        List<ProcessType> processTypeList = baseMapper.selectList(null);
        //遍历类型集合，根据审批分类id查询对应的审批模板
        for (ProcessType processType : processTypeList) {
            Long typeId = processType.getId();
            LambdaQueryWrapper<ProcessTemplate> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProcessTemplate::getProcessTypeId,typeId);
            List<ProcessTemplate> processTemplateList = processTemplateService.list(wrapper);

            //将审批模板数据封装到对应的审批类型对象中
            processType.setProcessTemplateList(processTemplateList);
        }

        return processTypeList;
    }
}
