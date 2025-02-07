package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseIdReq;
import com.nb6868.onex.common.validator.EnumValue;
import com.nb6868.onex.uc.UcConst;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户参数
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "用户请求")
public class ParamsSaveOrUpdateReq extends BaseIdReq {

    @Schema(description = "编码")
    @NotEmpty(message = "编码不能为空")
    private String code;

    @Schema(description = "类型")
    @EnumValue(enumClass = {UcConst.ParamsTypeEnum.class}, message = "类型传参错误")
    private Integer type;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "数据开放范围")
    @EnumValue(enumClass = {UcConst.ParamsScopeEnum.class}, message = "数据开放范围传参错误")
    private String scope;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "租户编码")
    private String tenantCode;

    @Schema(description = "备注")
    private String remark;

}
