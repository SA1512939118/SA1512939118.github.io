package com.zh.process.controller.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.auth.service.SysUserService;
import com.zh.common.result.Result;
import com.zh.model.process.Process;
import com.zh.model.process.ProcessTemplate;
import com.zh.model.process.ProcessType;
import com.zh.process.service.OaProcessService;
import com.zh.process.service.OaProcessTemplateService;
import com.zh.process.service.OaProcessTypeService;
import com.zh.vo.process.ApprovalVo;
import com.zh.vo.process.ProcessFormVo;
import com.zh.vo.process.ProcessVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author zh
 * @version 1.0
 * @date 2023/12/4 19:51
 */
@Api(tags = "审批流程管理")
@RestController
@RequestMapping("/admin/process")
@CrossOrigin//解决跨域
public class ProcessController {

    @Resource
    private OaProcessTypeService processTypeService;

    @Resource
    private OaProcessTemplateService processTemplateService;

    @Resource
    private OaProcessService processService;

    @Resource
    private SysUserService sysUserService;

    @ApiOperation(value = "待处理")
    @GetMapping("/findPending/{page}/{limit}")
    public Result findPendind(@PathVariable Long page,
                              @PathVariable Long limit){
        Page<Process> pageParam = new Page<>(page,limit);
        IPage<ProcessVo> pageModel = processService.findPending(pageParam);
        return Result.ok(pageModel);
    }

    //启动流程实例
    @ApiOperation(value = "启动流程")
    @PostMapping("/startUp")
    public Result startUp(@RequestBody ProcessFormVo processFormVo){
        processService.startUp(processFormVo);
        return Result.ok();
    }

    //获取审批模板数据
    @GetMapping("getProcessTemplate/{processTemplateId}")
    public Result getProcessTemplate(@PathVariable Long processTemplateId){
        ProcessTemplate processTemplate = processTemplateService.getById(processTemplateId);
        return Result.ok(processTemplate);
    }

    @GetMapping("findProcessType")
    public Result findAllProcessType(){
        //查询所有审批分类和每个分类所有的审批模板
        List<ProcessType> list = processTypeService.findProcessType();
        return Result.ok(list);
    }

    //查看审批详细信息
    @GetMapping("show/{id}")
    public Result show(@PathVariable Long id) {
        Map<String,Object> map = processService.show(id);
        return Result.ok(map);
    }

    //审批
    @ApiOperation(value = "审批")
    @PostMapping("approve")
    public Result approve(@RequestBody ApprovalVo approvalVo){
        processService.approve(approvalVo);
        return Result.ok();
    }

    @ApiOperation(value = "已处理")
    @GetMapping("/findProcessed/{page}/{limit}")
    public Result findProcessed(@PathVariable Long page,
                                @PathVariable Long limit){
        Page<Process> pageParam = new Page<>(page, limit);
        IPage<ProcessVo> pageModel = processService.findProcessed(pageParam);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "已发起")
    @GetMapping("/findStarted/{page}/{limit}")
    public Result findStarted(@PathVariable Long page,
                              @PathVariable Long limit){
        Page<ProcessVo> pageParam = new Page<>(page, limit);
        IPage<ProcessVo> pageModel =  processService.findStarted(pageParam);
        return Result.ok(pageModel);
    }

    @GetMapping("getCurrentUser")
    public Result getCurrentUser(){
        Map<String,Object> map = sysUserService.getCurrentUser();
        return Result.ok(map);
    }
}
