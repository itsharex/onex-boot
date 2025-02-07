package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.pojo.BaseDTO;
import com.nb6868.onex.uc.UcConst;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户参数
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "用户参数")
public class ParamsDTO extends BaseDTO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "数据开放范围")
    private String scope;

    public String getScopeName() {
        return UcConst.ParamsScopeEnum.getTitleByCode(scope);
    }

    @Schema(description = "类型")
    private Integer type;

    public String getTypeName() {
        return UcConst.ParamsTypeEnum.getTitleByCode(type);
    }

    @Schema(description = "编码")
    private String code;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "租户编码")
    private String tenantCode;

    @Schema(description = "备注")
    private String remark;

}
