package com.nb6868.onex.sys.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.PageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "字典查询")
public class DictQueryReq extends PageReq {

    @Query
    @Schema(description = "PID")
    private Long pid;

    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column = "name,code,value,remark")
    @Schema(description = "搜索关键词")
    private String search;

}
