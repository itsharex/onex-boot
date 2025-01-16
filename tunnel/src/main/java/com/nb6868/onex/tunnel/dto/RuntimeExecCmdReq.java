package com.nb6868.onex.tunnel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "系统参数查询")
public class RuntimeExecCmdReq {

    @Schema(description = "命令")
    @NotBlank(message = "命令不能为空")
    private String cmd;

}
