package com.nb6868.onex.common.pojo;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "Code请求")
public class CodeReq extends BaseReq {

    @Query
    @Schema(description = "code")
    @NotEmpty(message = "code不能为空", groups = DefaultGroup.class)
    private String code;

}
