package com.nb6868.onex.websocket.dto;

import com.nb6868.onex.common.pojo.BaseReq;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "websocket发送请求")
public class WebSocketSendReq extends BaseReq {

    /**
     * 单点发送校验
     */
    public interface SendOneGroup {
    }

    /**
     * 多点发送校验
     */
    public interface SendMultiGroup {
    }

    @Schema(description = "发送对象")
    @NotEmpty(message = "发送对象不能为空", groups = SendMultiGroup.class)
    private List<String> sidList;

    @Schema(description = "发送对象")
    @NotEmpty(message = "发送对象不能为空", groups = SendOneGroup.class)
    private String sid;

    @Schema(description = "发送内容", required = true)
    @NotNull(message = "发送内容不能为空", groups = DefaultGroup.class)
    private String content;

}
