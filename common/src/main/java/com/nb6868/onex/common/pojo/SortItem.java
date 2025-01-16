package com.nb6868.onex.common.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "基础排序请求")
public class SortItem implements Serializable {

    public SortItem(String column) {
        this.column = column;
    }

    @Schema(description = "排序字段")
    private String column;

    @Schema(description = "升序true/倒序false")
    private Boolean asc = true;

}
