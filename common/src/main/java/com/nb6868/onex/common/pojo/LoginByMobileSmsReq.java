package com.nb6868.onex.common.pojo;

import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.TenantGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "手机号验证码登录请求")
public class LoginByMobileSmsReq extends BaseReq {

    @Schema(description = "登录配置编码", example = "ADMIN_MOBILE_SMS")
    @NotEmpty(message = "登录类型不能为空", groups = {DefaultGroup.class})
    private String type;

    @Schema(description = "手机号")
    @NotEmpty(message = "手机号不能为空", groups = {DefaultGroup.class})
    private String mobile;

    @Schema(description = "验证码")
    @NotEmpty(message = "验证码不能为空", groups = {DefaultGroup.class})
    private String sms;

    @Schema(description = "租户编码")
    @NotEmpty(message = "租户编码不能为空", groups = {TenantGroup.class})
    private String tenantCode;

}
