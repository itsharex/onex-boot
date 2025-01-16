package com.nb6868.onex.job.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.PageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "定时任务记录查询")
public class JobLogQueryReq extends PageReq {

    @Query
    @Schema(description = "任务ID")
    private Long taskId;

    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column = "job_code,result,error")
    @Schema(description = "关键词搜索")
    private String search;

    @Query
    @Schema(description = "状态")
    private Integer state;

    @Query
    @Schema(description = "租户编码")
    private String tenantCode;

}
