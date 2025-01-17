package com.nb6868.onex.common.pojo;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "ID数组请求")
public class IdsReq extends BaseReq {

    @Query(type = Query.Type.IN, column = "id")
    @Schema(description = "ids")
    @NotEmpty(message = "ID参数不能为空")
    private List<Long> ids;

}
