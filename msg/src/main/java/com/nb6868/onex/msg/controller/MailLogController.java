package com.nb6868.onex.msg.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.pojo.CommonForm;
import com.nb6868.onex.common.pojo.PageData;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.msg.MsgConst;
import com.nb6868.onex.msg.dto.MailLogDTO;
import com.nb6868.onex.msg.dto.MailSendForm;
import com.nb6868.onex.msg.service.MailLogService;
import com.nb6868.onex.msg.service.MailTplService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotEmpty;
import javax.validation.groups.Default;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/msg/mailLog")
@Validated
@Api(tags = "消息记录", position = 20)
public class MailLogController {

    @Autowired
    MailLogService mailLogService;
    @Autowired
    MailTplService mailTplService;

    @GetMapping("page")
    @ApiOperation("分页列表")
    @RequiresPermissions("msg:mailLog:info")
    @ApiOperationSupport(order = 20)
    public Result<?> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<MailLogDTO> page = mailLogService.pageDto(params);

        return new Result<>().success(page);
    }

    @PostMapping("deleteBatch")
    @ApiOperation("批量删除")
    @LogOperation("批量删除")
    @RequiresPermissions("msg:mailLog:delete")
    @ApiOperationSupport(order = 50)
    public Result<?> deleteBatch(@Validated(value = {CommonForm.ListGroup.class}) @RequestBody CommonForm form) {
        mailLogService.logicDeleteByIds(form.getIds());

        return new Result<>();
    }

    @PostMapping("/send")
    @ApiOperation("发送消息")
    @LogOperation("发送消息")
    @RequiresPermissions("msg:mailLog:send")
    @ApiOperationSupport(order = 60)
    public Result<?> send(@Validated(value = {DefaultGroup.class}) @RequestBody MailSendForm dto) {
        boolean flag = mailLogService.send(dto);
        return new Result<>().boolResult(flag);
    }

    @PostMapping("sendCode")
    @ApiOperation("发送验证码消息")
    @LogOperation("发送验证码消息")
    @ApiOperationSupport(order = 70)
    public Result<?> sendCode(@Validated(value = {DefaultGroup.class}) @RequestBody MailSendForm dto) {
        // 只允许发送CODE_开头的模板
        AssertUtils.isFalse(dto.getTplCode().startsWith(MsgConst.SMS_CODE_TPL_PREFIX), "只支持" + MsgConst.SMS_CODE_TPL_PREFIX + "类型模板发送");
        boolean flag = mailLogService.send(dto);
        return new Result<>().boolResult(flag);
    }

}
