package com.nb6868.onex.cms.controller;

import com.nb6868.onex.cms.dto.ArticleCategoryDTO;
import com.nb6868.onex.cms.dto.ArticleCategoryQueryReq;
import com.nb6868.onex.cms.entity.ArticleCategoryEntity;
import com.nb6868.onex.cms.service.ArticleCategoryService;
import com.nb6868.onex.cms.service.ArticleService;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
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
 * 文章分类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@RestController
@RequestMapping("/cms/articleCategory")
@Validated
@Tag(name = "文章类目")
public class ArticleCategoryController {
    @Autowired
    private ArticleCategoryService articleCategoryService;
    @Autowired
    private ArticleService articleService;

    @PostMapping("list")
    @Operation(summary = "列表")
    @RequiresPermissions("cms:articleCategory:query")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> list(@Validated @RequestBody ArticleCategoryQueryReq form) {
        List<?> list = articleCategoryService.listDto(QueryWrapperHelper.getPredicate(form, "list"));

        return new Result<>().success(list);
    }

    @PostMapping("page")
    @Operation(summary = "分页")
    @RequiresPermissions("cms:articleCategory:query")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> page(@Validated({PageGroup.class}) @RequestBody ArticleCategoryQueryReq form) {
        PageData<?> page = articleCategoryService.pageDto(form, QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<>().success(page);
    }

    @PostMapping("info")
    @Operation(summary = "信息")
    @RequiresPermissions("cms:articleCategory:query")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> info(@Validated @RequestBody IdReq form) {
        ArticleCategoryDTO data = articleCategoryService.oneDto(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);

        return new Result<>().success(data);
    }

    @PostMapping("save")
    @Operation(summary = "保存")
    @LogOperation("保存")
    @RequiresPermissions("cms:articleCategory:edit")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> save(@Validated(value = {DefaultGroup.class, AddGroup.class}) @RequestBody ArticleCategoryDTO dto) {
        articleCategoryService.saveDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("update")
    @Operation(summary = "修改")
    @LogOperation("修改")
    @RequiresPermissions("cms:articleCategory:edit")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> update(@Validated(value = {DefaultGroup.class, UpdateGroup.class}) @RequestBody ArticleCategoryDTO dto) {
        articleCategoryService.updateDto(dto);

        return new Result<>().success(dto);
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    @LogOperation("删除")
    @RequiresPermissions("cms:articleCategory:delete")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    public Result<?> delete(@Validated @RequestBody IdReq form) {
        // 判断数据是否存在
        ArticleCategoryEntity data = articleCategoryService.getOne(QueryWrapperHelper.getPredicate(form));
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);
        // 检查是否存在子类和文章
        AssertUtils.isTrue(articleCategoryService.childrenCount(data.getId()) > 0, "存在子类,不允许删除");
        AssertUtils.isTrue(articleService.countByArticleCategoryId(data.getId()) > 0, "存在文章,不允许删除");
        articleCategoryService.removeById(data.getId(), false);
        return new Result<>();
    }

}
