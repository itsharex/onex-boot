package com.nb6868.onex.uc.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * 菜单权限
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "uc_menu", autoResultMap = true)
@Alias("uc_menu")
public class MenuEntity extends BaseEntity {

    /**
     * 上级ID，一级菜单为0
     */
	private Long pid;
    /**
     * 类型 0菜单/页面,1按钮/接口
     */
	private Integer type;
    /**
     * 名称
     */
	private String name;
    /**
     * 是否显示
     */
	private Integer showMenu;
    /**
     * 菜单或页面URL
     */
	private String url;
    /**
     * 菜单新页面打开
     */
	private Integer urlNewBlank;
    /**
     * 授权(多个用逗号分隔，如：sys:user:list,sys:user:save)
     */
	private String permissions;
    /**
     * 菜单图标
     */
	private String icon;
    /**
     * 排序
     */
	private Integer sort;
	/**
	 * 组件名称
	 */
	private String component;
	/**
	 * meta
	 */
	@TableField(typeHandler = JacksonTypeHandler.class)
	private JSONObject meta;
    /**
     * 租户编码
     */
	private String tenantCode;

}
