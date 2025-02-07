package com.nb6868.onex.websocket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "websocket连接")
public class SidDTO implements Serializable {

    @Schema(description = "SID")
    private String sid;

}
