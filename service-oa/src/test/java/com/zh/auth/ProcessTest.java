package com.zh.auth;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zh
 * @version 1.0
 * @date 2023/11/27 20:55
 */
@SpringBootTest
public class ProcessTest {

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Resource
    private HistoryService historyService;

    //实现全部流程挂起或激活
    @Test
    public void suspendProcessInstance(){
        //1.获取流程定义的对象
        ProcessDefinition qingjia = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("qingjia").singleResult();

        //2.查看流程对象是否挂起
        boolean suspended = qingjia.isSuspended();

        if (suspended){
            //如果挂起就激活
            //参数：1.流程定义id，2.布尔类型表示是否激活，4.激活时间点
            repositoryService
                    .activateProcessDefinitionById(qingjia.getId(),true,null);
            System.out.println(qingjia.getId()+"激活了");
        }else {
            //如果激活就挂起
            repositoryService
                    .suspendProcessDefinitionById(qingjia.getId(),true,null);
            System.out.println(qingjia.getId()+"挂起了");
        }
    }

    //实现单个流程实例挂起或激活
    @Test
    public void SingleSuspendProcessInstance() {
        String processInstanceId = "4bdeb2bf-8def-11ee-b0ab-005056c00001";
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        boolean suspended = processInstance.isSuspended();
        if (suspended){
            runtimeService.activateProcessInstanceById(processInstanceId);
            System.out.println("流程实例:" + processInstanceId + "激活");
        }else {
            runtimeService.suspendProcessInstanceById(processInstanceId);
            System.out.println("流程实例:" + processInstanceId + "挂起");
        }

    }

    //创建流程实例，指定业务标识BusinessKey
    @Test
    public void startProcessAddBusinessKey(){
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("qingjia", "1001");
        System.out.println(instance.getBusinessKey());
    }

    //查询已处理的任务
    @Test
    public void findCompleteTask(){
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee("lisi")
                .finished().list();
        for (HistoricTaskInstance historicTaskInstance : list) {
            System.out.println("实例id" + historicTaskInstance.getProcessInstanceId());
            System.out.println("任务id" + historicTaskInstance.getId());
            System.out.println("任务负责人" + historicTaskInstance.getAssignee());
            System.out.println("任务名称" + historicTaskInstance.getName());
        }
    }

    //处理当前任务
    @Test
    public void completeTask(){
        //先查询负责人需要处理的任务，返回一条
        Task task = taskService.createTaskQuery()
                .taskAssignee("zhanhsan")
                .singleResult();
        //完成任务，参数是任务id
        taskService.complete(task.getId());
    }

    //查询个人的待办任务--zhanhsan
    @Test
    public void findTaskList(){
        String assign = "zhanhsan";
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assign).list();
        for (Task task : list) {
            System.out.println("流程实例id"+task.getProcessInstanceId());
            System.out.println("任务id"+task.getId());
            System.out.println("任务负责人"+task.getAssignee());
            System.out.println("任务名称"+task.getName());
        }
    }

    //启动流程实例
    @Test
    public void startProcess(){
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("qingjia");
        System.out.println("流程定义ID"+processInstance.getProcessDefinitionId());
        System.out.println("流程实例ID"+processInstance.getProcessInstanceId());
        System.out.println("流程活动ID"+processInstance.getActivityId());
    }

    @Test
    public void deployProcess(){
        //流程部署，一个一个的部署
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/qingjia.bpmn20.xml")
                .addClasspathResource("process/qingjia.png")
                .name("请假申请流程")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());

    }
}
