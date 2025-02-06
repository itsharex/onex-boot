package com.nb6868.onex.uc.dto;

import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.BaseIdReq;
import com.nb6868.onex.common.validator.EnumValue;
import com.nb6868.onex.uc.UcConst;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

/**
 * 菜单权限
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "菜单权限")
public class MenuSaveOrUpdateReq extends BaseIdReq {

    @Schema(description = "上级ID，一级菜单为0")
    @NotNull(message = "请选择上级")
    private Long pid;

    @Schema(description = "类型 0菜单/页面,1按钮/接口")
    @EnumValue(enumClass = UcConst.MenuTypeEnum.class, message = "类型传参错误")
    private Integer type;

    @Schema(description = "名称")
    @NotBlank(message = "名称不能为空")
    private String name;

    @Schema(description = "排序")
    @Range(min = 0, max = 99999, message = "排序取值0-99999")
    private Integer sort;

    @Schema(description = "是否显示")
    @EnumValue(intValues = {0, 1}, message = "是否显示传参错误")
    private Integer showMenu;

    @Schema(description = "菜单新页面打开")
    @EnumValue(intValues = {0, 1}, message = "菜单新页面打开传参错误")
    private Integer urlNewBlank;

    @Schema(description = "菜单或页面URL")
    private String url;

    @Schema(description = "组件名称")
    private String component;

    @Schema(description = "meta")
    private JSONObject meta;

    @Schema(description = "授权(多个用逗号分隔，如：sys:user:list,sys:user:save)")
    private String permissions;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "租户编码")
    private String tenantCode;

}
