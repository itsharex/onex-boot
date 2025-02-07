package com.nb6868.onex.sys.dto;

import com.nb6868.onex.common.pojo.BaseIdReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据字典
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "数据字典")
public class DictSaveOrUpdateReq extends BaseIdReq {
    private static final long serialVersionUID = 1L;

    @Schema(description = "上级ID，一级为0")
    @NotNull(message = "上级ID不能为空")
    private Long pid;

    @Schema(description = "字典类型")
    @NotBlank(message = "字典类型不能为空")
    private String type;

    @Schema(description = "字典名称")
    @NotBlank(message = "字典名称不能为空")
    private String name;

    @Schema(description = "字典值")
    private String value;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "排序")
    @Min(value = 0, message = "{sort.number}")
    private Integer sort;

}
