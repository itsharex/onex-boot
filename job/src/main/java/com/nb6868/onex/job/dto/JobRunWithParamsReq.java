package com.nb6868.onex.job.dto;

import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "定时任务立即执行,指定参数")
public class JobRunWithParamsReq extends BaseReq {

     @Schema(description = "id")
    @NotNull(message = "{id.require}")
    private Long id;

     @Schema(description = "参数")
    private JSONObject params;

}
