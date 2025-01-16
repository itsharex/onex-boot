package com.nb6868.onex.uc.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.pojo.IdReq;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import com.nb6868.onex.uc.dto.TenantDTO;
import com.nb6868.onex.uc.dto.TenantQueryReq;
import com.nb6868.onex.uc.entity.TenantEntity;
import com.nb6868.onex.uc.service.TenantService;
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
@RequestMapping("/uc/tenant")
@Validated
@Tag(name = "租户管理")
public class TenantController {
    @Autowired
    private TenantService tenantService;

    @PostMapping("page")
    @Operation(summary = "分页")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:tenant:query"}, logical = Logical.OR)
    @ApiOperationSupport(order = 10)
    public Result<?> page(@Validated({PageGroup.class}) @RequestBody TenantQueryReq form) {
        QueryWrapper<TenantEntity> queryWrapper = QueryWrapperHelper.getPredicate(form, "page");
        PageData<?> page = tenantService.pageDto(form, queryWrapper);

        return new Result<>().success(page);
    }

    @PostMapping("list")
    @Operation(summary = "列表")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:tenant:query"}, logical = Logical.OR)
    @ApiOperationSupport(order = 20)
    public Result<?> list(@Validated @RequestBody TenantQueryReq form) {
        QueryWrapper<TenantEntity> queryWrapper = QueryWrapperHelper.getPredicate(form);
        List<?> list = tenantService.listDto(queryWrapper);

        return new Result<>().success(list);
    }

    @PostMapping("info")
    @Operation(summary = "信息")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:tenant:query"}, logical = Logical.OR)
    @ApiOperationSupport(order = 30)
    public Result<?> info(@Validated @RequestBody IdReq form) {
        TenantDTO data = tenantService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @Operation(summary = "保存")
    @LogOperation("保存")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:tenant:edit"}, logical = Logical.OR)
    @ApiOperationSupport(order = 40)
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody TenantDTO dto) {
        tenantService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("update")
    @Operation(summary = "修改")
    @LogOperation("修改")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:tenant:edit"}, logical = Logical.OR)
    @ApiOperationSupport(order = 50)
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody TenantDTO dto) {
        tenantService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    @LogOperation("删除")
    @RequiresPermissions(value = {"admin:super", "admin:uc", "uc:tenant:delete"}, logical = Logical.OR)
    @ApiOperationSupport(order = 100)
    public Result<?> delete(@Validated @RequestBody IdReq form) {
        tenantService.removeById(form.getId());
        // 按业务需求做其它操作
        return new Result<>();
    }

}
