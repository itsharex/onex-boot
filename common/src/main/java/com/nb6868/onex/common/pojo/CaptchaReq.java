package com.nb6868.onex.common.pojo;

import com.nb6868.onex.common.validator.group.CaptchaGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "带有验证码的请求")
public class CaptchaReq extends BaseReq {

    @Schema(description = "验证码值")
    @NotEmpty(message = "验证码不能为空", groups = {CaptchaGroup.class})
    private String captchaValue;

    @Schema(description = "验证码标识")
    @NotEmpty(message = "验证码标识不能为空", groups = {CaptchaGroup.class})
    private String captchaUuid;

}
