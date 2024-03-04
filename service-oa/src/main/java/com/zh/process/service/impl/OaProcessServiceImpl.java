package com.zh.process.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.auth.service.SysUserService;
import com.zh.common.result.Result;
import com.zh.model.process.Process;
import com.zh.model.process.ProcessRecord;
import com.zh.model.process.ProcessTemplate;
import com.zh.model.system.SysUser;
import com.zh.process.mapper.OaProcessMapper;
import com.zh.process.service.OaProcessRecordService;
import com.zh.process.service.OaProcessService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zh.process.service.OaProcessTemplateService;
import com.zh.security.custom.LoginUserInfoHelper;
import com.zh.vo.process.ApprovalVo;
import com.zh.vo.process.ProcessFormVo;
import com.zh.vo.process.ProcessQueryVo;
import com.zh.vo.process.ProcessVo;
import io.swagger.annotations.ApiOperation;
import org.activiti.bpmn.model.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;
import java.io.InputStream;
import java.security.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author zh
 * @since 2023-12-04
 */
@Service
public class OaProcessServiceImpl extends ServiceImpl<OaProcessMapper, Process> implements OaProcessService {

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private OaProcessTemplateService processTemplateService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Resource
    private OaProcessRecordService processRecordService;

    @Resource
    private HistoryService historyService;


    //审批管理列表
    @Override
    public IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo) {
        IPage<ProcessVo> pageModel = baseMapper.selectPage(pageParam,processQueryVo);
        return pageModel;
    }

    @Override
    public void deployByZip(String deployPath) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(deployPath);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        Deployment deployment = repositoryService.createDeployment().addZipInputStream(zipInputStream).deploy();
        System.out.println("deployment.getId() = " + deployment.getId());
        System.out.println("deployment.getName() = " + deployment.getName());
    }

    //启动流程实例
    @Override
    public void startUp(ProcessFormVo processFormVo) {
        //1.根据当前用户id获取用户信息
        SysUser sysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());

        //2.根据审批模板id把模板信息查出来
        ProcessTemplate processTemplate = processTemplateService.getById(processFormVo.getProcessTemplateId());

        //3.在当前业务表oa_process保存提交的审批信息
        Process process = new Process();
        //processFormVo复制到process中
        BeanUtils.copyProperties(processFormVo,process);
        //其他值
        process.setStatus(1);//审批中
        String workNo = System.currentTimeMillis() + "";
        process.setProcessCode(workNo);
        process.setUserId(LoginUserInfoHelper.getUserId());
        process.setFormValues(processFormVo.getFormValues());
        process.setTitle(sysUser.getUsername() + "发起" + processTemplate.getName() + "申请");
        baseMapper.insert(process);

        //4.启动流程实例 - RuntimeService
        //4.1 流程定义key
        String processDefinitionKey = processTemplate.getProcessDefinitionKey();

        //4.2 业务key processId
        String businessKey = String.valueOf(process.getId());

        //4.3 流程参数 form表单json数据，转换map集合
        String formValues = processFormVo.getFormValues();
        //formData
        JSONObject jsonObject = JSON.parseObject(formValues);
        JSONObject formData = jsonObject.getJSONObject("formData");//前端传来的json格式数据结构，名字固定的就是formData。里面有一系列k-v值
        HashMap<String, Object> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : formData.entrySet()) {
            map.put(entry.getKey(),entry.getValue());
        }
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("data",map);

        //启动流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);

        //5.查询下一个审批人是谁，后续整合微信前端之后，会给他发送提示信息
        //审批人可能会有多个
        List<org.activiti.engine.task.Task> taskList = this.getCurrentTaskList(processInstance.getId());
        List<String> nameList = new ArrayList<>();
        for (Task task : taskList) {
            String assigneeName = task.getAssignee();
            SysUser user = sysUserService.getUserByUsername(assigneeName);
            String name = user.getName();
            nameList.add(name);
        }
        //TODO 6.推送消息


        //7.业务和流程关联 更新oa_process
        process.setProcessInstanceId(processInstance.getId());
        process.setDescription("等待" + StringUtils.join(nameList,",") + "审批");
        baseMapper.updateById(process);

        //记录操作审批信息记录
        processRecordService.record(process.getId(),1,"发起申请");
    }

    //查询待处理任务列表
    @Override
    public IPage<ProcessVo> findPending(Page<Process> pageParam) {
        //1.封装查询的条件，根据当前登录的用户名称
        TaskQuery query = taskService.createTaskQuery()
                .taskAssignee(LoginUserInfoHelper.getUsername())
                .orderByTaskCreateTime()
                .desc();

        //2.调用方法分页条件查询，但会list集合，待办任务集合
        int begin = (int)((pageParam.getCurrent()-1) * pageParam.getSize());//开始位置
        int size = (int)pageParam.getSize();//每页显示记录数
        List<Task> taskList = query.listPage(begin, size);
        long totalCount = query.count();

        //3.封装返回List<ProcessVo>集合数据
        List<ProcessVo> processVoList = new ArrayList<>();
        for (Task task : taskList) {
            //从task获取流程实例id
            String processInstanceId = task.getProcessInstanceId();

            //根据流程实例id获取实例对象
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();

            //从流程实例对象中获取业务key
            String businessKey = processInstance.getBusinessKey();
            if (businessKey == null){
                continue;
            }
            //根据业务key获取Process对象
            Long processId = Long.parseLong(businessKey);
            Process process = baseMapper.selectById(processId);

            //把Process对象 复制 到ProcessVo中
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process,processVo);
            processVo.setTaskId(task.getId());
            processVoList.add(processVo);
        }

        //4.封装返回IPage对象
        IPage<ProcessVo> page = new Page<>(pageParam.getCurrent(),pageParam.getSize(),totalCount);
        page.setRecords(processVoList);
        return page;
    }

    //查看审批详细信息
    @Override
    public Map<String, Object> show(Long id) {
        //1.根据流程id获取流程信息
        Process process = baseMapper.selectById(id);

        //2.根据流程id获取流程记录信息
        LambdaQueryWrapper<ProcessRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProcessRecord::getProcessId,id);
        List<ProcessRecord> processRecordList = processRecordService.list(wrapper);

        //3.根据模板id查询模板信息
        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());

        //4.判断当前用户是否可以审批（可以看到信息的人不一定能审批，另外，也不能重复审批）
        boolean isApprove = false;
        List<Task> currentTaskList = this.getCurrentTaskList(process.getProcessInstanceId());
        String username = LoginUserInfoHelper.getUsername();
        for (Task task : currentTaskList) {
            if (task.getAssignee().equals(username)){
                isApprove = true;
            }
        }

        //4.查询数据库封装到map集合，返回
        HashMap<String, Object> map = new HashMap<>();
        map.put("process",process);
        map.put("processRecordList",processRecordList);
        map.put("processTemplate",processTemplate);
        map.put("isApprove",isApprove);
        return map;
    }

    //审批
    @Override
    public void approve(ApprovalVo approvalVo) {
        //1.从approvalVo中获取任务id，根据任务id获取流程变量
        String taskId = approvalVo.getTaskId();
        Map<String, Object> variables = taskService.getVariables(taskId);
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }

        //2.判断当前审批状态
        if (approvalVo.getStatus() == 1){
            //2.1 状态值 = 1 审批通过
            HashMap<String, Object> variable = new HashMap<>();
            taskService.complete(taskId,variable);//可以在完成任务的时候设置流程变量，此处没有设置，流程变量为空
        }else{
            //2.2 状态值 = -1 驳回，流程直接结束
            this.endTask(taskId);
        }

        //3.记录审批相关过程信息 oa_process_record
        String description = approvalVo.getStatus().intValue() == 1 ? "已通过" : "驳回";
        processRecordService.record(approvalVo.getProcessId(),approvalVo.getStatus(),description);


        //4.查询下一个审批人，更新流程表记录process表记录
        Process process = baseMapper.selectById(approvalVo.getProcessId());
        List<Task> taskList = this.getCurrentTaskList(process.getProcessInstanceId());
        if (!CollectionUtils.isEmpty(taskList)){
            ArrayList<String> assignList = new ArrayList<>();
            for (Task task : taskList) {
                String assignee = task.getAssignee();
                SysUser sysUser = sysUserService.getUserByUsername(assignee);
                assignList.add(sysUser.getName());

                //TODO 公众号消息推送
            }
            //更新process流程信息
            process.setDescription("等待" + StringUtils.join(assignList.toArray(),",") + "审批");
            process.setStatus(1);
        }else{
            if (approvalVo.getStatus().intValue() == 1){
                process.setDescription("审批完成（通过）");
                process.setStatus(2);
            }else{
                process.setDescription("审批完成（驳回）");
                process.setStatus(-1);
            }
        }
        baseMapper.updateById(process);
    }

    //查询已处理任务
    @Override
    public IPage<ProcessVo> findProcessed(Page<Process> pageParam) {
        //封装查询条件
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(LoginUserInfoHelper.getUsername())
                .finished()
                .orderByTaskCreateTime()
                .desc();

        //调用方法条件分页查询，返回list集合
        int begin = (int)((pageParam.getCurrent()-1) * pageParam.getSize());
        int size = (int)pageParam.getSize();
        List<HistoricTaskInstance> list = query.listPage(begin, size);
        long totalCount = query.count();

        //遍历返回的list集合，封装成List<ProcessVo>集合
        List<ProcessVo> processVoList = new ArrayList<>();
        for (HistoricTaskInstance item : list) {
            //获取流程实例id
            String processInstanceId = item.getProcessInstanceId();

            //根据流程实例id查询process信息
            LambdaQueryWrapper<Process> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Process::getProcessInstanceId,processInstanceId);
            Process process = baseMapper.selectOne(wrapper);

            //process -> processVo
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process,processVo);

            processVoList.add(processVo);
        }
        //IPage封装分页查询所有数据，返回
        IPage<ProcessVo> pageModel = new Page<>(pageParam.getCurrent(),pageParam.getSize(),totalCount);
        pageModel.setRecords(processVoList);

        return pageModel;
    }

    //查询已发起的任务流程
    @Override
    public IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam) {

        ProcessQueryVo processQueryVo = new ProcessQueryVo();
        processQueryVo.setUserId(LoginUserInfoHelper.getUserId());
        IPage<ProcessVo> pageModel = baseMapper.selectPage(pageParam,processQueryVo);
        return pageModel;
    }

    //结束任务流程
    private void endTask(String taskId) {
        //1.根据任务id获取任务task
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        //2.获取流程定义模型
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());

        //3.获取结束流向节点
        List<EndEvent> endEventList = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
        if (CollectionUtils.isEmpty(endEventList)){
            return;
        }
        FlowNode endFlowNode = (FlowNode)endEventList.get(0);

        //4.获取当前流向节点
        FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());

        //临时保存当前活动的原始方向
        List originSequenceFlowList = new ArrayList<>();
        originSequenceFlowList.add(currentFlowNode.getOutgoingFlows());
        //5.清理当前流动方向
        currentFlowNode.getOutgoingFlows().clear();

        //6.创建新的流向
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlow");
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(endFlowNode);

        //7当前节点指向新方向
        List newSequenceFlowList = new ArrayList<>();
        newSequenceFlowList.add(newSequenceFlow);
        currentFlowNode.setOutgoingFlows(newSequenceFlowList);

        //8.完成当前任务
        taskService.complete(taskId);
    }

    //当前任务列表
    private List<Task> getCurrentTaskList(String id) {
        List<org.activiti.engine.task.Task> taskList = taskService.createTaskQuery().processInstanceId(id).list();
        return taskList;
    }
}
