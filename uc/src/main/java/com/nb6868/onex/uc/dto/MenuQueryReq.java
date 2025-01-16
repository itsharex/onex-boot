package com.nb6868.onex.uc.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "菜单查询")
public class MenuQueryReq extends BaseReq {

    @Query
    @Schema(description = "类型")
    private Integer type;

    @Query(type = Query.Type.EQ_STRICT_EMPTY)
    @Schema(description = "租户编码,对租户无效")
    private String tenantCode;

}
