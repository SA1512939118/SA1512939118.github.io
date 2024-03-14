package com.zh.process.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.common.result.Result;
import com.zh.model.process.ProcessTemplate;
import com.zh.process.service.OaProcessTemplateService;
import io.netty.util.internal.ResourcesUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.coyote.http11.filters.SavedRequestInputFilter;
import org.springframework.remoting.jaxws.LocalJaxWsServiceFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

/**
 * <p>
 * 审批模板 前端控制器
 * </p>
 *
 * @author zh
 * @since 2023-11-30
 */
@RestController
@RequestMapping("/admin/process/processTemplate")
public class OaProcessTemplateController {

    @Resource
    private OaProcessTemplateService processTemplateService;

    @ApiOperation("获取分页审批模板数据")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page,
                        @PathVariable Long limit){
        Page<ProcessTemplate> pageParam = new Page<>(page, limit);
        //分页查询审批模板，把审批类型对应名称查询到，并封装进ProcessTemplate中的属性中去
        IPage<ProcessTemplate> pageModel =
                processTemplateService.selectPageProcessTemplate(pageParam);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id){
        ProcessTemplate processTemplate = processTemplateService.getById(id);
        return Result.ok(processTemplate);
    }

    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody ProcessTemplate processTemplate){
        processTemplateService.save(processTemplate);
        return Result.ok();
    }

    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result update(@RequestBody ProcessTemplate processTemplate){
        processTemplateService.updateById(processTemplate);
        return Result.ok();
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id){
        processTemplateService.removeById(id);
        return Result.ok();
    }

    @ApiOperation(value = "上传流程定义")
    @PostMapping("/uploadProcessDefinition")
    public Result uploadProcessDefinition(MultipartFile file) throws FileNotFoundException{
        //获取class目录位置
        String path = new File(ResourceUtils.getURL("classpath:").getPath()).getAbsolutePath();
        //设置上传文件夹
        File tempFile = new File(path + "/processes/");
        if (!tempFile.exists()){
            tempFile.mkdirs();
        }
        //创建空文件，实现文件写入
        String fileName = file.getOriginalFilename();
        File zipFile = new File(path + "/processes/" + fileName);

        //保存文件
        try {
            file.transferTo(zipFile);
        } catch (IOException e) {
            return Result.fail();
        }

        HashMap<String, Object> map = new HashMap<>();
        //根据上传地址后续部署流程定义，文件名称为流程定义的默认key
        map.put("processDefinitionPath","processes/"+fileName);
        map.put("processDefinitionKey", fileName.substring(0, fileName.lastIndexOf(".")));
        return Result.ok(map);
    }

    @ApiOperation(value = "发布")
    @GetMapping("/publish/{id}")
    public Result publish(@PathVariable Long id){
        //修改模板发布状态，1表示已经发布
        processTemplateService.publish(id);
        return Result.ok();
    }
}

