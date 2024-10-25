package com.nb6868.onex.common.shiro;

import cn.hutool.core.util.StrUtil;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 基础ShiroRealm
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public abstract class BaseShiroRealm extends AuthorizingRealm {

    @Autowired
    ShiroDao shiroDao;

    /**
     * 必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token != null;
    }

    /**
     * 从AuthenticationToken中获得token字符串
     */
    protected String getTokenFromAuthenticationToken(AuthenticationToken authenticationToken) {
        // AuthenticationToken包含身份信息和认证信息，在Filter中塞入
        String token = null;
        if (null != authenticationToken.getCredentials()) {
            token = authenticationToken.getCredentials().toString();
        }
        // 适配token中带有Bearer的情况
        return StrUtil.removePrefixIgnoreCase(token, "Bearer ");
    }

    /**
     * 授权(验证权限时调用)
     * 验证token不会过这个方法
     * 只有当需要检测用户权限的时候才会调用此方法
     * 例如RequiresPermissions/checkRole/checkPermission
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        ShiroUser shiroUser = (ShiroUser) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // 根据配置中的role和permission设置SimpleAuthorizationInfo
        if (null != shiroUser.getLoginConfig() && shiroUser.getLoginConfig().getBool("permissionBase", true)) {
            // 塞入权限列表,超级管理员全部
            List<String> permissionsList = shiroUser.isFullPermissions() ? shiroDao.getAllPermissionsList(shiroUser.getTenantCode()) : shiroDao.getPermissionsListByUserId(shiroUser.getId());
            permissionsList.forEach(permissions -> info.addStringPermissions(StrUtil.splitTrim(permissions, ",")));
        }
        if (null != shiroUser.getLoginConfig() && shiroUser.getLoginConfig().getBool("roleIdBase", false)) {
            // 塞入角色ID列表,超级管理员全部
            List<Long> roleList = shiroUser.isFullRoles() ? shiroDao.getAllRoleIdList(shiroUser.getTenantCode()) : shiroDao.getRoleIdListByUserId(shiroUser.getId());
            roleList.forEach(aLong -> info.addRole(aLong.toString()));
        }
        if (null != shiroUser.getLoginConfig() && shiroUser.getLoginConfig().getBool("roleCodeBase", false)) {
            // 塞入角色Code列表,超级管理员全部
            List<String> roleCodeList = shiroUser.isFullRoles() ? shiroDao.getAllRoleCodeList(shiroUser.getTenantCode()) : shiroDao.getRoleCodeListByUserId(shiroUser.getId());
            info.addRoles(roleCodeList);
        }
        return info;
    }

}
