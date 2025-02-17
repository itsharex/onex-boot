package com.nb6868.onex.sys.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 日志
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "sys_log", autoResultMap = true)
public class LogEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /**
     * 用户名
     */
    @TableField(fill = FieldFill.INSERT)
    private String createName;
    /**
     * 类型
     */
    private String type;
    /**
     * 请求URI
     */
    private String uri;
    /**
     * 内容
     */
    private String content;
    /**
     * 用户操作
     */
    private String operation;
    /**
     * 耗时(毫秒)
     */
    private Long requestTime;
    /**
     * 请求参数
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private JSONObject requestParams;
    /**
     * 请求体
     */
    // @TableField(typeHandler = JacksonTypeHandler.class)
    private String requestBody;
    /**
     * 状态
     */
    private Integer state;
    /**
     * 租户编码
     */
    private String tenantCode;

}
