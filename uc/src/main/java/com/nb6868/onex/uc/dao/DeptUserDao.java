package com.nb6868.onex.uc.dao;

import com.nb6868.onex.common.jpa.BaseDao;
import com.nb6868.onex.uc.entity.DeptUserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 部门用户关系
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface DeptUserDao extends BaseDao<DeptUserEntity> {

}
