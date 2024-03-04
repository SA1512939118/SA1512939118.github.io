package com.zh.process.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.model.process.Process;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zh.vo.process.ProcessQueryVo;
import com.zh.vo.process.ProcessVo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 审批类型 Mapper 接口
 * </p>
 *
 * @author zh
 * @since 2023-12-04
 */
public interface OaProcessMapper extends BaseMapper<Process> {
    //查询审批管理列表
    IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, @Param("vo") ProcessQueryVo processQueryVo);
}
