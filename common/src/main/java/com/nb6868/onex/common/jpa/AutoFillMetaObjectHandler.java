package com.nb6868.onex.common.jpa;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.nb6868.onex.common.shiro.ShiroUser;
import com.nb6868.onex.common.shiro.ShiroUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 公共字段，自动填充值
 * see {<a href="https://mybatis.plus/guide/auto-fill-metainfo.html">...</a>}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@ConditionalOnProperty(name = "onex.auto-fill.enable", havingValue = "true")
@Component
public class AutoFillMetaObjectHandler implements MetaObjectHandler {

    /**
     * 创建时间
     */
    protected final static String CREATE_TIME = "createTime";
    /**
     * 创建者id
     */
    protected final static String CREATE_ID = "createId";
    /**
     * 创建者名字
     */
    protected final static String CREATE_NAME = "createName";
    /**
     * 更新时间
     */
    protected final static String UPDATE_TIME = "updateTime";
    /**
     * 更新者id
     */
    protected final static String UPDATE_ID = "updateId";
    /**
     * 更新者名字
     */
    protected final static String UPDATE_NAME = "updateName";
    /**
     * 所在部门id
     */
    protected final static String DEPT_ID = "deptId";
    /**
     * 租户编码
     */
    protected final static String TENANT_CODE = "tenantCode";
    /**
     * 删除标记
     */
    protected final static String DELETED = "deleted";

    /**
     * 插入时填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        ShiroUser user = ShiroUtils.getUser();
        Date now = new Date();

        strictInsertFill(metaObject, DELETED, Long.class, 0L);
        strictInsertFill(metaObject, CREATE_TIME, Date.class, now);
        strictInsertFill(metaObject, UPDATE_TIME, Date.class, now);
        /*if (metaObject.hasGetter(DEPT_ID) && metaObject.getValue(DEPT_ID) == null && user.getDeptId() != null) {
            strictInsertFill(metaObject, DEPT_ID, Long.class, user.getDeptId());
        }*/
        if (metaObject.hasGetter(TENANT_CODE) && StrUtil.isNotBlank(user.getTenantCode()) &&  !ObjectUtil.isEmpty(metaObject.getValue(TENANT_CODE))) {
            // 存在租户编码字段,并且用户存在租户信息,并且未指定租户信息
            // Entity中需要定义tenantCode为@TableField(fill = FieldFill.INSERT)
            strictInsertFill(metaObject, TENANT_CODE, String.class, user.getTenantCode());
        }
        strictInsertFill(metaObject, CREATE_ID, Long.class, user.getId());
        strictInsertFill(metaObject, CREATE_NAME, String.class, user.getUsername());
        strictInsertFill(metaObject, UPDATE_ID, Long.class, user.getId());
        strictInsertFill(metaObject, UPDATE_NAME, String.class, user.getUsername());
    }

    /**
     * 更新时填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        ShiroUser user = ShiroUtils.getUser();
        Date now = new Date();
        strictUpdateFill(metaObject, UPDATE_TIME, Date.class, now);
        strictUpdateFill(metaObject, UPDATE_ID, Long.class, user.getId());
        strictUpdateFill(metaObject, UPDATE_NAME, String.class, user.getUsername());
    }

}
