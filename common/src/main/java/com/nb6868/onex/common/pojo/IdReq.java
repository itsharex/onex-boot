package com.nb6868.onex.common.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onex.common.jpa.Query;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "ID查询请求")
public class IdReq extends BaseReq {

    @Query
    @Schema(description = "id")
    @NotNull(message = "ID参数不能为空")
    private Long id;

    @Query(type = Query.Type.LIMIT)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer pageSize = 1;

}
