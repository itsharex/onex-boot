package com.nb6868.onex.common.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通过验证码修改密码请求
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "通过验证码修改密码请求")
public class ChangePasswordByMailCodeReq extends BaseReq {

    @Schema(description = "登录配置编码", example = "ADMIN_DINGTALK_CODE")
    @NotEmpty(message = "登录类型不能为空")
    private String type;

    @Schema(description = "新密码")
    @NotEmpty(message = "新密码不能为空")
    private String newPasswordEncrypted;

    @Schema(description = "验证码")
    @NotEmpty(message = "验证码不能为空")
    private String smsCode;

    @Schema(description = "收件方")
    @NotEmpty(message = "收件方不能为空")
    private String mailTo;

}
