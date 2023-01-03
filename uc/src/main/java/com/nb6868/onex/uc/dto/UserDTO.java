package com.nb6868.onex.uc.dto;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nb6868.onex.common.pojo.BaseDTO;
import com.nb6868.onex.common.validator.group.AddGroup;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "用户")
public class UserDTO extends BaseDTO {

	@ApiModelProperty(value = "类型")
	private Integer type;

	@ApiModelProperty(value = "部门编码")
	private String deptCode;

	@ApiModelProperty(value = "区域编码")
	private String areaCode;

	@ApiModelProperty(value = "岗位编码")
	private String postCode;

	@ApiModelProperty(value = "状态")
	private Integer state;

	@ApiModelProperty(value = "编号")
	private String code;

	@ApiModelProperty(value = "等级")
	private String level;

	@ApiModelProperty(value = "用户名")
	@NotBlank(message = "{username.require}", groups = DefaultGroup.class)
	private String username;

	@ApiModelProperty(value = "密码")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@NotBlank(message = "{password.require}", groups = AddGroup.class)
	private String password;

	@ApiModelProperty(value = "真实姓名")
	private String realName;

	@ApiModelProperty(value = "昵称")
	private String nickname;

	@ApiModelProperty(value = "手机号")
	private String mobile;

	@ApiModelProperty(value = "邮箱")
	private String email;

	@ApiModelProperty(value = "头像")
	private String avatar;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "账户余额")
	private BigDecimal balance;

	@ApiModelProperty(value = "积分")
	private BigDecimal points;

	@ApiModelProperty(value = "收入余额")
	private BigDecimal income;

	@ApiModelProperty(value = "租户编码")
	private String tenantCode;

	@ApiModelProperty(value = "部门链")
	private List<DeptDTO> deptChain;

	@ApiModelProperty(value = "角色名称")
	private String roleNames;

	@ApiModelProperty(value = "角色ID列表")
	private List<Long> roleIds;

	@ApiModelProperty(value = "额外信息")
	private JSONObject extInfo;

	@ApiModelProperty(value = "第三方帐号信息")
	private JSONObject oauthInfo;

	@ApiModelProperty(value = "第三方帐号用户id")
	private String oauthUserid;

}
