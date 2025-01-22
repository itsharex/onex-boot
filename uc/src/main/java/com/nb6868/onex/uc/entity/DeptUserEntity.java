package com.nb6868.onex.uc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nb6868.onex.common.pojo.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * 角色-部门用户关系
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("uc_dept_user")
@Alias("uc_dept_user")
public class DeptUserEntity extends BaseEntity {

    /**
     * 部门ID
     */
	private Long deptId;
    /**
     * 用户ID
     */
	private Long userId;
    /**
     * 关系类型
     */
    private Integer type;

}
