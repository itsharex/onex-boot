package com.nb6868.onex.sys.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.QueryWrapperHelper;
import com.nb6868.onex.common.oss.AbstractOssService;
import com.nb6868.onex.common.oss.AliyunOssUploadCallbackReq;
import com.nb6868.onex.common.oss.OssFactory;
import com.nb6868.onex.common.oss.OssPropsConfig;
import com.nb6868.onex.common.params.BaseParamsService;
import com.nb6868.onex.common.pojo.ApiResult;
import com.nb6868.onex.common.pojo.IdsReq;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.util.MultipartFileUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.PageGroup;
import com.nb6868.onex.sys.SysConst;
import com.nb6868.onex.sys.dto.OssFileBase64UploadReq;
import com.nb6868.onex.sys.dto.OssPreSignedReq;
import com.nb6868.onex.sys.dto.OssQueryReq;
import com.nb6868.onex.sys.dto.OssSignedPostReq;
import com.nb6868.onex.sys.entity.OssEntity;
import com.nb6868.onex.sys.service.OssService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController("SysOss")
@RequestMapping("/sys/oss/")
@Validated
@Tag(name = "存储管理")
@Slf4j
public class OssController {

    @Autowired
    OssService ossService;
    @Autowired
    BaseParamsService paramsService;

    @PostMapping("upload")
    @Operation(summary = "上传文件(文件形式)")
    public Result<?> upload(@RequestParam(required = false, defaultValue = SysConst.OSS_PUBLIC) String paramsCode,
                            @RequestParam(required = false) String prefix,
                            @RequestPart MultipartFile file) {
        AssertUtils.isTrue(file.isEmpty(), ErrorCode.UPLOAD_FILE_EMPTY);
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(paramsCode, OssPropsConfig.class, null);
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");
        String objectKey = uploadService.buildObjectKey(prefix, file.getOriginalFilename());
        ApiResult<JSONObject> uploadResult = uploadService.upload(objectKey, file);
        AssertUtils.isFalse(uploadResult.isSuccess(), uploadResult.getCodeMsg());
        Dict result = Dict.create().set("src", ossConfig.getDomain() + objectKey).set("filename", file.getOriginalFilename());
        if (ossConfig.getSaveDb()) {
            //保存文件信息
            OssEntity oss = new OssEntity();
            oss.setUrl(ossConfig.getDomain() + objectKey);
            oss.setFilename(file.getOriginalFilename());
            oss.setSize(file.getSize());
            oss.setContentType(file.getContentType());
            ossService.save(oss);
            result.set("oss", oss);
        }
        return new Result<>().success(result);
    }

    @PostMapping("uploadToTemp")
    @Operation(summary = "上传到临时文件", description = "不支持分布式环境")
    public Result<?> uploadTemp(@RequestPart MultipartFile file) {
        AssertUtils.isTrue(file.isEmpty(), ErrorCode.UPLOAD_FILE_EMPTY);
        File localFile = MultipartFileUtils.multipartFileToFile(file);
        AssertUtils.isNull(localFile, ErrorCode.OSS_UPLOAD_FILE_ERROR);

        Dict result = Dict.create().set("filePath", localFile.getAbsolutePath()).set("filename", file.getOriginalFilename());
        return new Result<>().success(result);
    }

    @PostMapping("uploadExcelToTemp")
    @Operation(summary = "上传Excel到临时文件(文件形式)", description = "不支持分布式环境")
    public Result<?> uploadExcelToTemp(@RequestPart MultipartFile file) {
        AssertUtils.isTrue(file.isEmpty(), ErrorCode.UPLOAD_FILE_EMPTY);
        File localFile = MultipartFileUtils.multipartFileToFile(file);
        AssertUtils.isFalse(localFile != null && localFile.exists(), ErrorCode.OSS_UPLOAD_FILE_ERROR);

        List<Object> titles = new ArrayList<>();
        try {
            ExcelReader reader = ExcelUtil.getReader(localFile);
            titles = reader.readRow(0);
        } catch (Exception e) {
            log.error("Excel文件读取失败", e);
        }
        Dict result = Dict.create().set("filePath", localFile.getAbsolutePath()).set("columns", titles).set("filename", file.getOriginalFilename());
        return new Result<>().success(result);
    }

    @PostMapping("uploadBase64")
    @Operation(summary = "上传单文件(base64)")
    public Result<?> uploadBase64(@Validated @RequestBody OssFileBase64UploadReq form) {
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(form.getParamsCode(), OssPropsConfig.class, null);
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");

        // 有两个前缀，config定义前缀和用户上传前缀
        String objectKey = uploadService.buildObjectKey(form.getPrefix(), form.getFilaName());
        ApiResult<JSONObject> uploadResult = uploadService.uploadBase64(objectKey, form.getFileBase64());
        AssertUtils.isFalse(uploadResult.isSuccess(), uploadResult.getCodeMsg());
        Dict result = Dict.create().set("src", ossConfig.getDomain() + objectKey).set("filename", form.getFilaName());
        if (ossConfig.getSaveDb()) {
            //保存文件信息
            OssEntity oss = new OssEntity();
            oss.setUrl(ossConfig.getDomain() + objectKey);
            oss.setFilename(form.getFilaName());
            oss.setSize(0L);
            oss.setContentType(FileUtil.getMimeType(form.getFilaName()));
            ossService.save(oss);
            result.set("oss", oss);
        }
        return new Result<>().success(result);
    }

    @PostMapping("uploadMulti")
    @Operation(summary = "上传多文件")
    public Result<?> uploadMulti(@RequestParam(required = false, defaultValue = SysConst.OSS_PUBLIC) String paramsCode,
                                 @RequestParam(required = false) String prefix,
                                 @RequestPart @NotEmpty(message = "文件不能为空") MultipartFile[] files) {
        List<String> srcList = new ArrayList<>();
        List<OssEntity> ossList = new ArrayList<>();
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(paramsCode, OssPropsConfig.class, null);
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");
        // 有两个前缀，config定义前缀和用户上传前缀
        for (MultipartFile file : files) {
            // 上传文件
            String objectKey = uploadService.buildObjectKey(prefix, file.getOriginalFilename());
            ApiResult<JSONObject> uploadResult = uploadService.upload(objectKey, file);
            if (uploadResult.isSuccess() && ossConfig.getSaveDb()) {
                //保存文件信息
                OssEntity oss = new OssEntity();
                oss.setUrl(ossConfig.getDomain() + objectKey);
                oss.setFilename(file.getOriginalFilename());
                oss.setSize(file.getSize());
                oss.setContentType(file.getContentType());
                ossService.save(oss);
                srcList.add(ossConfig.getDomain() + objectKey);
                ossList.add(oss);
            }
        }

        return new Result<>().success(Dict.create().set("src", CollUtil.join(srcList, ",")).set("oss", ossList));
    }

    @PostMapping("aliyunUploadCallback")
    @Operation(summary = "阿里云上传回调")
    @AccessControl
    public Result<?> aliyunUploadCallback(@Validated @RequestBody AliyunOssUploadCallbackReq req) {
        // todo 处理回调请求结果
        return new Result<>();
    }

    @PostMapping("getSignedPostForm")
    @Operation(summary = "获得已签名的post表单参数")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:oss"}, logical = Logical.OR)
    public Result<?> getSignedPostForm(@Validated @RequestBody OssSignedPostReq form) {
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(form.getParamsCode(), OssPropsConfig.class, null);
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");

        ApiResult<JSONObject> result = uploadService.getSignedPostForm(form.getConditions(), form.getExpire(), null);
        AssertUtils.isFalse(result.isSuccess(), result.getCodeMsg());
        return new Result<>().success(result.getData());
    }

    @PostMapping("getPreSignedPostForm")
    @Operation(summary = "获得已签名的post表单参数")
    public Result<?> getPreSignedPostForm(@Validated @RequestBody OssPreSignedReq form) {
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(StrUtil.emptyToDefault(form.getParamsCode(), SysConst.OSS_PUBLIC), OssPropsConfig.class, null);
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");

        String objectKey = uploadService.buildObjectKey(form.getPrefix(), form.getFileName());
        ApiResult<JSONObject> result = uploadService.getSignedPostForm(null, form.getExpire(), objectKey);
        AssertUtils.isFalse(result.isSuccess(), result.getCodeMsg());
        return new Result<>().success(result.getData());
    }

    @PostMapping("getPreSignedUrl")
    @Operation(summary = "获得授权访问地址")
    public Result<?> getPreSignedUrl(@Validated @RequestBody OssPreSignedReq form) {
        OssPropsConfig ossConfig = paramsService.getSystemPropsObject(form.getParamsCode(), OssPropsConfig.class, null);
        AbstractOssService uploadService = OssFactory.build(ossConfig);
        AssertUtils.isNull(uploadService, "未定义的上传方式");

        String objectKey = uploadService.buildObjectKey(form.getPrefix(), form.getFileName());
        ApiResult<String> result = uploadService.getPreSignedUrl(objectKey, form.getMethod(), form.getExpire());
        AssertUtils.isFalse(result.isSuccess(), result.getCodeMsg());
        return new Result<>().success(result.getData());
    }

    @PostMapping("page")
    @Operation(summary = "分页")
    @QueryDataScope(tenantFilter = true, tenantValidate = false)
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:oss", "sys:oss:query"}, logical = Logical.OR)
    public Result<?> page(@Validated({PageGroup.class}) @RequestBody OssQueryReq form) {
        PageData<?> page = ossService.pageDto(form, QueryWrapperHelper.getPredicate(form, "page"));

        return new Result<>().success(page);
    }

    @PostMapping("deleteBatch")
    @Operation(summary = "批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions(value = {"admin:super", "admin:sys", "admin:oss", "sys:oss:delete"}, logical = Logical.OR)
    public Result<?> deleteBatch(@Validated @RequestBody IdsReq req) {
        ossService.remove(QueryWrapperHelper.getPredicate(req));

        return new Result<>();
    }

}
