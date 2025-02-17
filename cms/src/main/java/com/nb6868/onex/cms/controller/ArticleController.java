package com.nb6868.onex.cms.controller;

import com.nb6868.onex.cms.dto.ArticleDTO;
import com.nb6868.onex.cms.dto.ArticleQueryReq;
import com.nb6868.onex.cms.service.ArticleService;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 文章管理
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/cms/article")
@Validated
@Tag(name = "文章")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping("list")
    @Operation(summary = "列表")
    @RequiresPermissions("cms:article:query")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> list(@Validated @RequestBody ArticleQueryReq form) {
        List<?> list = articleService.listDto(QueryWrapperHelper.getPredicate(form, "list"));

        return new Result<>().success(list);
    }

    @PostMapping("page")
    @Operation(summary = "分页")
    @RequiresPermissions("cms:article:query")
    public Result<?> page(@Validated({PageGroup.class}) @RequestBody ArticleQueryReq form) {
        PageData<?> page = articleService.pageDto(form, QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<>().success(page);
    }

    @PostMapping("info")
    @Operation(summary = "信息")
    @RequiresPermissions("cms:article:query")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> info(@Validated @RequestBody IdReq form) {
        ArticleDTO data = articleService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @Operation(summary = "保存")
    @LogOperation("保存")
    @RequiresPermissions("cms:article:edit")
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody ArticleDTO dto) {
        articleService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("update")
    @Operation(summary = "修改")
    @LogOperation("修改")
    @RequiresPermissions("cms:article:edit")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody ArticleDTO dto) {
        articleService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    @LogOperation("删除")
    @RequiresPermissions("cms:article:delete")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> delete(@Validated @RequestBody IdReq req) {
        articleService.removeById(req.getId(), false);

        return new Result<>();
    }

    @PostMapping("deleteBatch")
    @Operation(summary = "批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("cms:article:delete")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> deleteBatch(@Validated @RequestBody IdsReq req) {
        articleService.removeByIds(req.getIds(), false);

        return new Result<>();
    }

}
