package com.zh.process.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.common.result.Result;
import com.zh.model.process.Process;
import com.zh.process.service.OaProcessService;
import com.zh.vo.process.ProcessQueryVo;
import com.zh.vo.process.ProcessVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 审批类型 前端控制器
 * </p>
 *
 * @author zh
 * @since 2023-12-04
 */
@RestController
@RequestMapping("/admin/process")
public class OaProcessController {

    @Resource
    private OaProcessService processService;

    //审批管理列表
    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page,
                        @PathVariable Long limit,
                        ProcessQueryVo processQueryVo){
        Page<ProcessVo> pageParam = new Page<>(page, limit);
        IPage<ProcessVo>  pageModel = processService.selectPage(pageParam,processQueryVo);
        return Result.ok(pageModel);

    }
}

