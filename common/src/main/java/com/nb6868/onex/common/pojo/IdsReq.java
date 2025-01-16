package com.nb6868.onex.common.pojo;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "ID数组请求")
public class IdsReq extends BaseReq {

    @Query(type = Query.Type.IN, column = "id")
    @Schema(description = "ids")
    @Null(message = "ID参数不能值", groups = AddGroup.class)
    @NotNull(message = "ID参数不能为空", groups = {UpdateGroup.class, DefaultGroup.class})
    private List<Long> ids;

}
