package com.nb6868.onex.common.pojo;

import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.TenantGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "Code登录请求")
public class LoginByCodeReq extends BaseReq {

    @Schema(description = "登录配置编码", example = "ADMIN_DINGTALK_CODE")
    @NotEmpty(message = "登录类型不能为空", groups = {DefaultGroup.class})
    private String type;

    @Schema(description = "登录编码")
    @NotEmpty(message = "登录编码不能为空", groups = {DefaultGroup.class})
    private String code;

    @Schema(description = "租户编码")
    @NotEmpty(message = "租户编码不能为空", groups = {TenantGroup.class})
    private String tenantCode;

}
