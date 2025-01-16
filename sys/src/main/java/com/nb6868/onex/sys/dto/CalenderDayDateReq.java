package com.nb6868.onex.sys.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "日期查询请求")
public class CalenderDayDateReq extends BaseReq {

    @Query
    @Schema(description = "dayDate", required = true)
    @NotBlank(message = "查询日期不能为空")
    private Date dayDate;

    @Query(type = Query.Type.LIMIT)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer pageSize = 1;

}
