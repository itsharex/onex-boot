package com.nb6868.onex.uc.service;

import cn.hutool.core.collection.CollUtil;
import com.nb6868.onex.common.jpa.EntityService;
import com.nb6868.onex.uc.dao.DeptUserDao;
import com.nb6868.onex.uc.entity.DeptUserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 部门用户关系表
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class DeptUserService extends EntityService<DeptUserDao, DeptUserEntity> {

    /**
     * 保存或修改
     *
     * @param userId  用户ID
     * @param deptIds 部门ID数组
     * @param type 关系类型
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateByUserIdAndDeptIds(Long userId, List<Long> deptIds, Integer type) {
        // 先删除角色用户关系
        deleteByUserIdList(CollUtil.toList(userId));

        // 保存角色用户关系
        CollUtil.distinct(deptIds).forEach(deptId -> {
            DeptUserEntity entity = new DeptUserEntity();
            entity.setUserId(userId);
            entity.setDeptId(deptId);
            save(entity);
        });
        return true;
    }

    /**
     * 根据部门ids，删除部门用户关系
     *
     * @param deptIds 部门ids
     */
    public boolean deleteByDeptIdList(List<Long> deptIds) {
        if (CollUtil.isEmpty(deptIds)) {
            return true;
        }
        return remove(lambdaQuery().in(DeptUserEntity::getDeptId, deptIds));
    }

    /**
     * 根据用户id，删除角色用户关系
     *
     * @param userIds 用户ids
     */
    public boolean deleteByUserIdList(List<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return true;
        }
        return remove(lambdaQuery().in(DeptUserEntity::getUserId, userIds));
    }

}
