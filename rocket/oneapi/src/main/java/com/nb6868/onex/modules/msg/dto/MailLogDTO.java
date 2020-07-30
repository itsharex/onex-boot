package com.nb6868.onex.modules.msg.dto;

import com.nb6868.onex.booster.pojo.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 消息记录
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "消息记录")
public class MailLogDTO extends BaseDTO {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "模板ID")
	private Long tplId;

	@ApiModelProperty(value = "模板编码")
	private String tplCode;

	@ApiModelProperty(value = "消息类型")
	private String tplType;

	@ApiModelProperty(value = "发送者")
	private String mailFrom;

	@ApiModelProperty(value = "收件人")
	private String mailTo;

	@ApiModelProperty(value = "标题")
	private String subject;

	@ApiModelProperty(value = "内容参数")
	private String contentParams;

	@ApiModelProperty(value = "内容")
	private String content;

	@ApiModelProperty(value = "消费状态 0 :  未消费 1 ：已消费")
	private Integer consumeStatus;

	@ApiModelProperty(value = "发送状态  0：失败  1：成功")
	private Integer status;

	@ApiModelProperty(value = "发送结果")
	private String result;

}
