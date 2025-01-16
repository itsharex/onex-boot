package com.nb6868.onex.sys.dto;

import com.nb6868.onex.common.pojo.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "获得STS临时访问token请求")
public class OssStsReq extends BaseReq {

    @Schema(description = "配置参数名")
    private String paramsCode = "OSS_PRIVATE";

}
