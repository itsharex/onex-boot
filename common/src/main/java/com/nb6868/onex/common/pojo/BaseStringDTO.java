package com.nb6868.onex.common.pojo;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import jakarta.validation.constraints.Null;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础DTO
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
public abstract class BaseStringDTO implements Serializable {

    @Schema(description = "id")
    @Null(message = "{id.null}", groups = AddGroup.class)
    @NotEmpty(message = "{id.require}", groups = UpdateGroup.class)
    private String id;

    @Schema(description = "创建者ID")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long createId;

    @Schema(description = "创建时间")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date createTime;

    @Schema(description = "更新者ID")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long updateId;

    @Schema(description = "更新时间")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date updateTime;

    @Schema(description = "逻辑删除")
    @JsonIgnore
    private Long deleted;

    @Schema(description = "是否存在id，用来判断还是新增")
    @JsonIgnore
    public boolean hasId() {
        return StrUtil.isNotBlank(id);
    }

}
