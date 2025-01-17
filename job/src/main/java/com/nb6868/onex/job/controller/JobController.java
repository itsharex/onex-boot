package com.nb6868.onex.job.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.IdReq;
import com.nb6868.onex.common.pojo.IdsReq;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.job.dto.JobDTO;
import com.nb6868.onex.job.dto.JobLogDTO;
import com.nb6868.onex.job.dto.JobLogQueryReq;
import com.nb6868.onex.job.dto.JobQueryReq;
import com.nb6868.onex.job.dto.JobRunWithParamsReq;
import com.nb6868.onex.job.service.JobLogService;
import com.nb6868.onex.job.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys/job/")
@Validated
@Tag(name = "定时任务")
public class JobController {

    @Autowired
    JobService jobService;
    @Autowired
    JobLogService jobLogService;

    @PostMapping("page")
    @Operation(summary = "分页")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:job", "sys:job:query"}, logical = Logical.OR)
    @ApiOperationSupport(order = 10)
    public Result<?> page(@Validated({PageGroup.class}) @RequestBody JobQueryReq form) {
        PageData<?> page = jobService.pageDto(form, QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<>().success(page);
    }

    @PostMapping("info")
    @Operation(summary = "详情")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:job", "sys:job:query"}, logical = Logical.OR)
    @ApiOperationSupport(order = 20)
    public Result<?> info(@Validated @RequestBody IdReq form) {
        JobDTO data = jobService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @Operation(summary = "保存")
    @LogOperation("保存")
    @RequiresPermissions(value = {"admin:super", "admin:job", "sys:job:edit"}, logical = Logical.OR)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @ApiOperationSupport(order = 30)
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody JobDTO dto) {
        jobService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("update")
    @Operation(summary = "修改")
    @LogOperation("修改")
    @ApiOperationSupport(order = 40)
    @RequiresPermissions(value = {"admin:super", "admin:job", "sys:job:edit"}, logical = Logical.OR)
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody JobDTO dto) {
        jobService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    @LogOperation("删除")
    @ApiOperationSupport(order = 50)
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:job", "sys:job:delete"}, logical = Logical.OR)
    public Result<?> delete(@Validated @RequestBody IdReq req) {
        JobDTO data = jobService.oneDto(QueryWrapperHelper.getPredicate(req));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);
        // 删除数据
        jobService.remove(QueryWrapperHelper.getPredicate(req));
        return new Result<>();
    }

    @PostMapping("/runWithParams")
    @Operation(summary = "指定参数立即执行")
    @LogOperation("指定参数立即执行")
    @ApiOperationSupport(order = 60)
    @RequiresPermissions(value = {"admin:super", "admin:job", "sys:job:run"}, logical = Logical.OR)
    public Result<?> runWithParams(@Validated @RequestBody JobRunWithParamsReq form) {
        jobService.runWithParams(form);

        return new Result<>();
    }

    @PostMapping("logPage")
    @Operation(summary = "日志分页")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:job", "sys:jobLog:query"}, logical = Logical.OR)
    @ApiOperationSupport(order = 100)
    public Result<?> logPage(@Validated({PageGroup.class}) @RequestBody JobLogQueryReq form) {
        PageData<?> page = jobLogService.pageDto(form, QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<>().success(page);
    }

    @PostMapping("logInfo")
    @Operation(summary = "日志详情")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:job", "sys:jobLog:query"}, logical = Logical.OR)
    @ApiOperationSupport(order = 110)
    public Result<?> logInfo(@Validated @RequestBody IdReq form) {
        JobLogDTO data = jobLogService.getDtoById(form.getId());
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("logDeleteBatch")
    @Operation(summary = "日志批量删除")
    @LogOperation("日志批量删除")
    @RequiresPermissions(value = {"admin:super", "admin:job", "sys:jobLog:delete"}, logical = Logical.OR)
    @ApiOperationSupport(order = 120)
    public Result<?> logDeleteBatch(@Validated @RequestBody IdsReq req) {
        jobLogService.remove(QueryWrapperHelper.getPredicate(req));

        return new Result<>();
    }

}
