package com.nb6868.onexboot.api.modules.uc.dao;

import com.nb6868.onexboot.common.dao.BaseDao;
import com.nb6868.onexboot.api.modules.uc.entity.RoleDataScopeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色数据权限
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Mapper
public interface RoleDataScopeDao extends BaseDao<RoleDataScopeEntity> {

    /**
     * 获取用户的部门数据权限列表
     */
    @Select("select uc_role_data_scope.dept_id from uc_role_user, uc_role_data_scope where uc_role_user.deleted = 0 and uc_role_data_scope.deleted = 0 and uc_role_user.user_id = #{userId} and uc_role_user.role_id = uc_role_data_scope.role_id")
    List<Long> getDeptIdListByUserId(@Param("userId") Long userId);

}
