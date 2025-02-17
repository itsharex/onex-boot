package com.nb6868.onex.msg.dto;

import com.nb6868.onex.common.pojo.BaseReq;
import com.nb6868.onex.msg.MsgConst;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.groups.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "短信发送请求")
public class MsgSmsSendReq extends BaseReq {

    @Schema(description = "模板编码", required = true, example = "CODE_LOGIN")
    @NotBlank(message = "模板编码不能为空", groups = Default.class)
    private String tplCode = MsgConst.SMS_TPL_LOGIN;

    @Schema(description = "收件人", required = true)
    @NotBlank(message = "收件人不能为空", groups = Default.class)
    private String mailTo;

}
