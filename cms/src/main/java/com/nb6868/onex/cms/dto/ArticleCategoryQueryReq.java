package com.nb6868.onex.cms.dto;

import com.nb6868.onex.common.jpa.Query;
import com.nb6868.onex.common.pojo.PageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "文章分类查询")
public class ArticleCategoryQueryReq extends PageReq {

    @Query(blurryType = Query.BlurryType.OR, type = Query.Type.LIKE, column = "name,code,descr,title")
    @Schema(description = "搜索关键词")
    private String search;

}
