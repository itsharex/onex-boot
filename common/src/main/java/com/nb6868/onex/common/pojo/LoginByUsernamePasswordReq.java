package com.nb6868.onex.common.pojo;

import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.TenantGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "用户名密码登录请求")
public class LoginByUsernamePasswordReq extends CaptchaReq {

    @Schema(description = "登录配置编码", example = "ADMIN_USERNAME_PASSWORD")
    @NotEmpty(message = "请指定登录类型", groups = {DefaultGroup.class})
    private String type;

    @Schema(description = "租户编码")
    @NotEmpty(message = "租户编码不能为空", groups = {TenantGroup.class})
    private String tenantCode;

    @Schema(description = "用户名")
    @NotEmpty(message = "用户名不能为空", groups = {DefaultGroup.class})
    private String username;

    @Schema(description = "密码(加密后)")
    @NotEmpty(message = "密码不能为空", groups = {DefaultGroup.class})
    private String passwordEncrypted;

}
