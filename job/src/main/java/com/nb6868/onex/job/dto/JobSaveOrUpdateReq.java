package com.nb6868.onex.job.dto;

import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.BaseIdReq;
import com.nb6868.onex.common.validator.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "定时任务表单请求")
public class JobSaveOrUpdateReq extends BaseIdReq {

    @Schema(description = "名称")
    @NotBlank(message = "{name.require}")
    private String code;

    @Schema(description = "状态")
    @EnumValue(intValues = {0, 1}, message = "状态值错误")
    private Integer state;

    @Schema(description = "cron表达式")
    @NotBlank(message = "cron表达式不能为空")
    private String cron;

    @Schema(description = "日志类型")
    @NotBlank(message = "日志类型不能为空")
    private String logType = "db";

    @Schema(description = "参数")
    private JSONObject params;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "租户编码")
    private String tenantCode;

}
