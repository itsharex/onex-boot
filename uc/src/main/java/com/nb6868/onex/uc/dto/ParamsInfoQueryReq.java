package com.nb6868.onex.uc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "参数查询")
public class ParamsInfoQueryReq extends BaseReq {

    @Schema(description = "参数编码")
    @NotEmpty(message = "编码不能为空")
    @Query
    private String code;

    @Schema(description = "租户编码")
    @Query
    private String tenantCode;

    @Schema(description = "URL地址")
    @Query(column = "content->'$.url'")
    private String contentUrl;

    @Query(type = Query.Type.LIMIT)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer pageSize = 1;

}
