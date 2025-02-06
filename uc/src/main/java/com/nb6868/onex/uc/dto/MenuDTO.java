package com.nb6868.onex.uc.dto;

import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 菜单权限
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "菜单权限")
public class MenuDTO extends BaseDTO {

    @Schema(description = "上级ID，一级菜单为0")
    private Long pid;

    @Schema(description = "类型 0菜单/页面,1按钮/接口")
    private Integer type;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "是否显示")
    private Integer showMenu;

    @Schema(description = "菜单或页面URL")
    private String url;

    @Schema(description = "组件名称")
    private String component;

    @Schema(description = "meta")
    private JSONObject meta;

    @Schema(description = "菜单新页面打开")
    private Integer urlNewBlank;

    @Schema(description = "授权(多个用逗号分隔，如：sys:user:list,sys:user:save)")
    private String permissions;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "租户编码")
    private String tenantCode;

    @Schema(description = "上级菜单列表")
    private List<MenuDTO> parentMenuList;
}
