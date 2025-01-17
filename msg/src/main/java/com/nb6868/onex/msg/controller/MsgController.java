package com.nb6868.onex.msg.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.msg.MsgSendForm;
import com.nb6868.onex.common.pojo.IdReq;
import com.nb6868.onex.common.pojo.IdsReq;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.msg.dto.MsgLogQueryReq;
import com.nb6868.onex.msg.dto.MsgTplDTO;
import com.nb6868.onex.msg.dto.MsgTplQueryReq;
import com.nb6868.onex.msg.service.MsgLogService;
import com.nb6868.onex.msg.service.MsgService;
import com.nb6868.onex.msg.service.MsgTplService;
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

import java.util.List;

@RestController
@RequestMapping("/sys/msg/")
@Validated
@Tag(name = "消息管理")
public class MsgController {

    @Autowired
    MsgService msgService;
    @Autowired
    MsgTplService msgTplService;
    @Autowired
    MsgLogService msgLogService;

    @PostMapping("tplPage")
    @Operation(summary = "模板分页")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:msg", "sys:msgTpl:query"}, logical = Logical.OR)
    @ApiOperationSupport(order = 20)
    public Result<?> tplPage(@Validated({PageGroup.class}) @RequestBody MsgTplQueryReq form) {
        PageData<?> page = msgTplService.pageDto(form, QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<>().success(page);
    }

    @PostMapping("tplList")
    @Operation(summary = "模板列表")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:msg", "sys:msgTpl:query"}, logical = Logical.OR)
    @ApiOperationSupport(order = 10)
    public Result<?> tplList(@Validated @RequestBody MsgTplQueryReq form) {
        List<?> list = msgTplService.listDto(QueryWrapperHelper.getPredicate(form));
        return new Result<>().success(list);
    }

    @PostMapping("tplInfo")
    @Operation(summary = "模板详情")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:msg", "sys:msgTpl:query"}, logical = Logical.OR)
    @ApiOperationSupport(order = 30)
    public Result<?> info(@Validated @RequestBody IdReq form) {
        MsgTplDTO data = msgTplService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("tplSave")
    @Operation(summary = "模板保存")
    @LogOperation("模板保存")
    @RequiresPermissions(value = {"admin:super", "admin:msg", "sys:msgTpl:edit"}, logical = Logical.OR)
    @ApiOperationSupport(order = 40)
    public Result<?> tplSave(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody MsgTplDTO dto) {
        msgTplService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("tplUpdate")
    @Operation(summary = "模板修改")
    @LogOperation("模板修改")
    @RequiresPermissions(value = {"admin:super", "admin:msg", "sys:msgTpl:edit"}, logical = Logical.OR)
    @ApiOperationSupport(order = 50)
    public Result<?> tplUpdate(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody MsgTplDTO dto) {
        msgTplService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("tplDelete")
    @Operation(summary = "模板删除")
    @LogOperation("模板删除")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:msg", "sys:msgTpl:delete"}, logical = Logical.OR)
    @ApiOperationSupport(order = 60)
    public Result<?> delete(@Validated(value = {DefaultGroup.class}) @RequestBody IdReq req) {
        msgTplService.remove(QueryWrapperHelper.getPredicate(req));

        return new Result<>();
    }

    @PostMapping("logPage")
    @Operation(summary = "日志分页")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:msg", "sys:msgLog:query"}, logical = Logical.OR)
    @ApiOperationSupport(order = 100)
    public Result<?> page(@Validated({PageGroup.class}) @RequestBody MsgLogQueryReq form) {
        PageData<?> page = msgLogService.pageDto(form, QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<>().success(page);
    }

    @PostMapping("send")
    @Operation(summary = "发送消息")
    @LogOperation("发送消息")
    @RequiresPermissions(value = {"admin:super", "admin:msg", "sys:msg:send"}, logical = Logical.OR)
    @ApiOperationSupport(order = 110)
    public Result<?> send(@Validated(value = {DefaultGroup.class}) @RequestBody MsgSendForm form) {
        boolean flag = msgService.sendMail(form);
        return new Result<>().bool(flag);
    }

    @PostMapping("logDeleteBatch")
    @Operation(summary = "记录批量删除")
    @LogOperation("记录批量删除")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:msg", "sys:msgLog:delete"}, logical = Logical.OR)
    @ApiOperationSupport(order = 50)
    public Result<?> logDeleteBatch(@Validated @RequestBody IdsReq req) {
        msgLogService.remove(QueryWrapperHelper.getPredicate(req));

        return new Result<>();
    }

}
