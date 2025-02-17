package com.nb6868.onex.common.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

/**
 * 更新状态请求
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "修改状态请求")
public class ChangeStateReq extends BaseIdReq {

    /**
     * 状态只能取值0-1
     */
    public interface BoolStateGroup {
    }

    @Schema(description = "状态")
    @Range(min = 0, max = 1, message = "状态值取值0-1", groups = BoolStateGroup.class)
    private Integer state;

    @Schema(description = "备注")
    private String remark;

}
