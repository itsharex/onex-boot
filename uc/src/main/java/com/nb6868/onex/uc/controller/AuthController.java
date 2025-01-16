package com.nb6868.onex.uc.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.nb6868.onex.common.annotation.AccessControl;
import com.nb6868.onex.common.annotation.LogOperation;
import com.nb6868.onex.common.auth.*;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.msg.BaseMsgService;
import com.nb6868.onex.common.msg.MsgLogBody;
import com.nb6868.onex.common.msg.MsgSendForm;
import com.nb6868.onex.common.msg.MsgTplBody;
import com.nb6868.onex.common.pojo.*;
import com.nb6868.onex.common.shiro.ShiroUser;
import com.nb6868.onex.common.shiro.ShiroUtils;
import com.nb6868.onex.common.util.*;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.common.validator.ValidatorUtils;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.dto.MenuResult;
import com.nb6868.onex.uc.dto.MenuScopeForm;
import com.nb6868.onex.uc.dto.MenuScopeResult;
import com.nb6868.onex.uc.dto.UserDTO;
import com.nb6868.onex.uc.entity.UserEntity;
import com.nb6868.onex.uc.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/uc/auth/")
@Validated
@Tag(name = "用户授权")
@Slf4j
public class AuthController {

    @Autowired
    AuthProps authProps;
    @Autowired
    UserService userService;
    @Autowired
    CaptchaService captchaService;
    @Autowired
    TokenService tokenService;
    @Autowired
    MenuService menuService;
    @Autowired
    ParamsService paramsService;
    @Autowired
    AuthService authService;
    @Autowired
    BaseMsgService msgService;
    @Autowired
    RoleUserService roleUserService;

    @PostMapping("captcha")
    @AccessControl
    @Operation(summary = "图形验证码(base64)", description = "Anon")
    @ApiOperationSupport(order = 10)
    public Result<?> captcha(@Validated @RequestBody BaseForm form) {
        // 获得登录验证码配置,设置默认杜绝空信息
        JSONObject captchaParams = paramsService.getSystemPropsObject("CAPTCHA_LOGIN", JSONObject.class, new JSONObject());
        // uuid是用来存和后续对比图片验证码的
        String uuid = IdUtil.fastSimpleUUID();
        // 生成图片，并将uuid和图片内容作为kv存入缓存
        String captchaBase64 = captchaService.createCaptchaBase64(uuid,
                captchaParams.getStr("type", "circle"),
                captchaParams.getStr("randomBase", "0123456789"),
                captchaParams.getInt("randomLength", 4),
                captchaParams.getInt("width", 110),
                captchaParams.getInt("height", 40));
        // 将uuid和图片base64返回给前端
        JSONObject result = new JSONObject().set("uuid", uuid).set("image", captchaBase64);
        return new Result<>().success(result);
    }

    @PostMapping("userLogin")
    @AccessControl
    @Operation(summary = "用户账号登录", description = "Anon")
    @LogOperation(value = "用户账号登录", type = "login")
    @ApiOperationSupport(order = 20)
    public Result<?> userLogin(@Validated(value = {DefaultGroup.class}) @RequestBody UserLoginForm form) {
        // 获得对应登录类型的登录参数
        JSONObject loginParams = paramsService.getSystemPropsJson(form.getType());
        AssertUtils.isNull(loginParams, "缺少[" + form.getType() + "]登录配置");
        // 验证验证码
        if (loginParams.getBool("captcha", false)) {
            // 先检验验证码表单
            ValidatorUtils.validateEntity(form, LoginForm.CaptchaGroup.class);
            // 再校验验证码与魔术验证码不同，并且 校验失败
            AssertUtils.isTrue(!form.getCaptchaValue().equalsIgnoreCase(loginParams.getStr("magicCaptcha")) && !captchaService.validate(form.getCaptchaUuid(), form.getCaptchaValue()), ErrorCode.CAPTCHA_ERROR);
        }
        UserEntity user = authService.loginByUsernamePassword(form, loginParams);
        // 创建token
        String token = tokenService.createToken(user,
                loginParams.getStr(AuthConst.TOKEN_STORE_TYPE_KEY, AuthConst.TOKEN_STORE_TYPE_VALUE),
                form.getType(),
                loginParams.getStr(AuthConst.TOKEN_JWT_KEY_KEY, AuthConst.TOKEN_JWT_KEY_VALUE),
                loginParams.getInt(AuthConst.TOKEN_EXPIRE_KEY, AuthConst.TOKEN_EXPIRE_VALUE),
                loginParams.getInt(AuthConst.TOKEN_LIMIT_KEY, AuthConst.TOKEN_LIMIT_VALUE));
        // 登录成功
        LoginResult loginResult = new LoginResult()
                .setUser(ConvertUtils.sourceToTarget(user, UserDTO.class))
                .setToken(token)
                .setTokenKey(authProps.getTokenHeaderKey());
        return new Result<>().success(loginResult);
    }

    @PostMapping("userLoginByDingtalkCode")
    @AccessControl
    @Operation(summary = "钉钉免密code登录", description = "Anon")
    @LogOperation(value = "钉钉免密code登录", type = "login")
    @ApiOperationSupport(order = 30)
    public Result<?> userLoginByCode(@Validated(value = {DefaultGroup.class}) @RequestBody CodeLoginForm form) {
        // 获得对应登录类型的登录参数
        JSONObject loginParams = paramsService.getSystemPropsJson(form.getType());
        AssertUtils.isNull(loginParams, "缺少[" + form.getType() + "]登录配置");
        AssertUtils.isTrue(StrUtil.hasBlank(loginParams.getStr("appId"), loginParams.getStr("appSecret")), "登录配置缺少appId和appSecret信息");

        ApiResult<String> userAccessToken = DingTalkApi.getUserAccessToken(loginParams.getStr("appId"), loginParams.getStr("appSecret"), form.getCode());
        AssertUtils.isTrue(userAccessToken.isSuccess(), userAccessToken.getMsg());
        ApiResult<JSONObject> userContact = DingTalkApi.getUserContact(userAccessToken.getData(), "me");
        AssertUtils.isTrue(userContact.isSuccess(), userContact.getMsg());
        ApiResult<JSONObject> userIdResponse = DingTalkApi.getUserIdByUnionid(userAccessToken.getData(), userContact.getData().getStr("unionId"));
        AssertUtils.isTrue(userIdResponse.isSuccess(), userIdResponse.getMsg());
        // 封装自己的业务逻辑,比如用userId去找用户
        UserEntity user = userService.query().eq("oauth_userid", userIdResponse.getData().getStr("userid")).last(Const.LIMIT_ONE).one();
        if (user == null) {
            // 不存在
            if (loginParams.getBool("autoCreateUserEnable", false)) {
                // 自动创建用户
                user = new UserEntity();
                user.setUsername(userContact.getData().getStr("nick"));
                user.setRealName(userContact.getData().getStr("nick"));
                user.setPassword(DigestUtil.bcrypt(userIdResponse.getData().getStr("userid")));
                user.setPasswordRaw(PasswordUtils.aesEncode(userIdResponse.getData().getStr("userid"), Const.AES_KEY));
                user.setOauthUserid(userIdResponse.getData().getStr("userid"));
                user.setOauthInfo(JSONUtil.parseObj(userContact.getData()));
                user.setMobile(userContact.getData().getStr("mobile"));
                user.setAvatar(userContact.getData().getStr("avatarUrl"));
                user.setType(UcConst.UserTypeEnum.DEPT_ADMIN.value());
                user.setState(UcConst.UserStateEnum.ENABLED.value());
                user.setTenantCode(form.getTenantCode());
                AssertUtils.isTrue(userService.hasDuplicated(null, "username", user.getUsername()), ErrorCode.ERROR_REQUEST, "用户名已存在");
                // AssertUtils.isTrue(userService.hasDuplicated(null, "mobile", user.getMobile()), ErrorCode.ERROR_REQUEST, "手机号已存在");
                userService.save(user);
                // 保存角色关系
                roleUserService.saveOrUpdateByUserIdAndRoleIds(user.getId(), loginParams.getBeanList("autoCreateUserRoleIds", Long.class));
            } else {
                return new Result<>().error("用户未注册");
            }
        }
        // 判断用户是否存在
        AssertUtils.isNull(user, ErrorCode.ACCOUNT_NOT_EXIST);
        // 判断用户状态
        AssertUtils.isFalse(user.getState() == UcConst.UserStateEnum.ENABLED.value(), ErrorCode.ACCOUNT_DISABLE);
        // 创建token
        String token = tokenService.createToken(user,
                loginParams.getStr(AuthConst.TOKEN_STORE_TYPE_KEY, AuthConst.TOKEN_STORE_TYPE_VALUE),
                form.getType(),
                loginParams.getStr(AuthConst.TOKEN_JWT_KEY_KEY, AuthConst.TOKEN_JWT_KEY_VALUE),
                loginParams.getInt(AuthConst.TOKEN_EXPIRE_KEY, AuthConst.TOKEN_EXPIRE_VALUE),
                loginParams.getInt(AuthConst.TOKEN_LIMIT_KEY, AuthConst.TOKEN_LIMIT_VALUE));
        // 登录成功
        LoginResult loginResult = new LoginResult()
                .setUser(ConvertUtils.sourceToTarget(user, UserDTO.class))
                .setToken(token)
                .setTokenKey(authProps.getTokenHeaderKey());
        return new Result<>().success(loginResult);
    }

    @PostMapping("sendMsgCode")
    @AccessControl
    @Operation(summary = "发送验证码消息", description = "Anon")
    @LogOperation("发送验证码消息")
    @ApiOperationSupport(order = 20)
    public Result<?> sendMsgCode(@Validated(value = {DefaultGroup.class}) @RequestBody MsgSendForm form) {
        MsgTplBody mailTpl = msgService.getTplByCode(form.getTenantCode(), form.getTplCode());
        AssertUtils.isNull(mailTpl, ErrorCode.ERROR_REQUEST, "消息模板不存在");
        if (mailTpl.getParams().getBool("verifyUserExist", false)) {
            // 是否先验证用户是否存在
            UserEntity user = userService.getByMobile(form.getTenantCode(), form.getMailTo());
            AssertUtils.isNull(user, ErrorCode.ACCOUNT_NOT_EXIST);
            AssertUtils.isFalse(user.getState() == UcConst.UserStateEnum.ENABLED.value(), ErrorCode.ACCOUNT_DISABLE);
        }
        // 结果标记
        boolean flag = msgService.sendMail(form);
        if (flag) {
            return new Result<>().success("短信发送成功", null);
        } else {
            return new Result<>().error("短信发送失败");
        }
    }

    /*@PostMapping("userLoginEncrypt")
    @AccessControl
    @Operation(summary = "用户登录(加密)", description = "Anon@将userLogin接口数据,做AES加密作为body的值")
    @LogOperation(value = "用户登录(加密)", type = "login")
    @ApiOperationSupport(order = 110)
    public Result<?> userLoginEncrypt(@Validated @RequestBody EncryptForm encryptForm) {
        LoginForm form = SignUtils.decodeAES(encryptForm.getBody(), Const.AES_KEY, LoginForm.class);
        return ((AuthController) AopContext.currentProxy()).userLogin(form);
    }*/

    @PostMapping("userLogout")
    @Operation(summary = "用户登出")
    @LogOperation(value = "用户登出", type = "logout")
    @ApiOperationSupport(order = 120)
    public Result<?> userLogout() {
        String token = HttpContextUtils.getRequestParameter(authProps.getTokenHeaderKey());
        tokenService.deleteToken(token);
        return new Result<>();
    }

    @PostMapping("userInfo")
    @Operation(summary = "用户信息")
    @ApiOperationSupport(order = 150)
    public Result<?> userInfo() {
        UserEntity user = userService.getById(ShiroUtils.getUserId());
        AssertUtils.isNull(user, ErrorCode.ACCOUNT_NOT_EXIST);

        UserDTO data = ConvertUtils.sourceToTarget(user, UserDTO.class);
        // todo 补上用户的其他信息
        // data.setRoleCodes(userService.getUserRoleCodes());
        return new Result<>().success(data);
    }

    @PostMapping("userChangePassword")
    @Operation(summary = "用户修改密码")
    @LogOperation("用户修改密码")
    @ApiOperationSupport(order = 160)
    public Result<?> userChangePassword(@Validated @RequestBody ChangePasswordForm form) {
        // 获得对应登录类型的登录参数
        JSONObject loginParams = paramsService.getSystemPropsJson(form.getType());
        AssertUtils.isNull(loginParams, "缺少[" + form.getType() + "]登录配置");
        // 先对密码做解密
        String passwordPlaintext = PasswordUtils.aesDecode(form.getPasswordEncrypted(), StrUtil.emptyToDefault(authProps.getTransferKey(), Const.AES_KEY));
        String newPasswordPlaintext = PasswordUtils.aesDecode(form.getNewPasswordEncrypted(), StrUtil.emptyToDefault(authProps.getTransferKey(), Const.AES_KEY));
        // 对新密码密码强度做校验
        // 密码复杂度正则
        AssertUtils.isTrue(StrUtil.isNotBlank(loginParams.getStr("passwordRegExp")) && !ReUtil.isMatch(loginParams.getStr("passwordRegExp"), newPasswordPlaintext), ErrorCode.ERROR_REQUEST, loginParams.getStr("passwordRegError", "密码不符合规则"));
        // 获取数据库中的用户
        UserEntity data = userService.getById(ShiroUtils.getUserId());
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);
        // 校验原密码
        AssertUtils.isFalse(PasswordUtils.verify(passwordPlaintext, data.getPassword()), ErrorCode.ACCOUNT_PASSWORD_ERROR);
        // 更新密码
        userService.updatePassword(data.getId(), newPasswordPlaintext, authProps.getPasswordStoreKey());
        // 注销该用户所有token,提示用户重新登录
        tokenService.deleteByUserIdList(Collections.singletonList(data.getId()));
        return new Result<>();
    }

    @PostMapping("userResetPassword")
    @AccessControl
    @Operation(summary = "用户重置密码(帐号找回)")
    @LogOperation("用户重置密码(帐号找回)")
    @ApiOperationSupport(order = 164)
    public Result<?> userResetPassword(@Validated @RequestBody ChangePasswordByMailCodeForm form) {
        // 获得对应登录类型的登录参数
        JSONObject loginParams = paramsService.getSystemPropsJson(form.getType());
        AssertUtils.isNull(loginParams, "缺少[" + form.getType() + "]登录配置");
        // 先对密码做解密
        String newPasswordPlaintext = PasswordUtils.aesDecode(form.getNewPasswordEncrypted(), StrUtil.emptyToDefault(authProps.getTransferKey(), Const.AES_KEY));
        // 密码复杂度正则
        AssertUtils.isTrue(StrUtil.isNotBlank(loginParams.getStr("passwordRegExp")) && !ReUtil.isMatch(loginParams.getStr("passwordRegExp"), newPasswordPlaintext), ErrorCode.ERROR_REQUEST, loginParams.getStr("passwordRegError", "密码不符合规则"));
        // 获取数据库中的用户
        UserEntity data = userService.getById(ShiroUtils.getUserId());
        AssertUtils.isNull(data, ErrorCode.DB_RECORD_NOT_EXISTED);
        // 校验短信
        MsgLogBody lastSmsLog = msgService.getLatestByTplCode(null, "CODE_LOGIN", form.getMailTo());
        AssertUtils.isTrue(lastSmsLog == null || !form.getSmsCode().equalsIgnoreCase(lastSmsLog.getContentParams().getStr("code")), ErrorCode.ERROR_REQUEST, "验证码错误");
        // 验证码正确,校验过期时间
        AssertUtils.isTrue(lastSmsLog.getValidEndTime() != null && lastSmsLog.getValidEndTime().before(new Date()), ErrorCode.ERROR_REQUEST, "验证码已过期");
        // 将短信消费掉
        msgService.consumeLog(lastSmsLog.getId());
        // 更新密码
        userService.updatePassword(data.getId(), newPasswordPlaintext, authProps.getPasswordStoreKey());
        // 注销该用户所有token,提示用户重新登录
        tokenService.deleteByUserIdList(Collections.singletonList(data.getId()));
        return new Result<>();
    }

    @PostMapping("userMenuScope")
    @Operation(summary = "用户权限范围", description = "返回包括菜单、路由、权限、角色等所有内容")
    @ApiOperationSupport(order = 200)
    public Result<MenuScopeResult> userMenuScope(@Validated @RequestBody MenuScopeForm form) {
        ShiroUser user = ShiroUtils.getUser();
        // 过滤出其中显示菜单
        List<TreeNode<Long>> menuList = new ArrayList<>();
        // 过滤出其中路由菜单
        List<MenuResult> urlList = new ArrayList<>();
        // 过滤出其中的权限
        List<String> permissions = new ArrayList<>();
        // 获取该用户所有menu
        menuService.getListByUser(user.getType(), user.getTenantCode(), user.getId(), null, null).forEach(menu -> {
            if (menu.getShowMenu() == 1 && menu.getType() == UcConst.MenuTypeEnum.MENU.value()) {
                // 菜单需要显示 && 菜单类型为菜单
                menuList.add(new TreeNode<>(menu.getId(), menu.getPid(), menu.getName(), menu.getSort()).setExtra(Dict.create().set("icon", menu.getIcon()).set("url", menu.getUrl()).set("urlNewBlank", menu.getUrlNewBlank())));
            }
            if (StrUtil.isNotBlank(menu.getUrl())) {
                urlList.add(ConvertUtils.sourceToTarget(menu, MenuResult.class));
            }
            if (form.isPermissions() && StrUtil.isNotBlank(menu.getPermissions())) {
                permissions.addAll(StrUtil.splitTrim(menu.getPermissions(), ','));
            }
        });
        // 将菜单列表转成菜单树
        List<Tree<Long>> menuTree = TreeNodeUtils.buildIdTree(menuList);
        MenuScopeResult result = new MenuScopeResult()
                .setMenuTree(menuTree)
                .setUrlList(urlList);
        // 塞入权限
        if (form.isPermissions()) {
            result.setPermissions(permissions);
        }
        // 塞入角色编码
        if (form.isRoleCodes()) {
            result.setRoleCodes(userService.getUserRoleCodes(user));
        }
        // 塞入角色id
        if (form.isRoleIds()) {
            result.setRoleIds(userService.getUserRoleIds(user));
        }
        return new Result<MenuScopeResult>().success(result);
    }

    @PostMapping("userMenuTree")
    @Operation(summary = "用户菜单树", description = "用户左侧显示菜单")
    @ApiOperationSupport(order = 230)
    public Result<?> userMenuTree() {
        ShiroUser user = ShiroUtils.getUser();
        List<TreeNode<Long>> menuList = new ArrayList<>();
        // 获取该用户所有menu, 菜单需要显示 && 菜单类型为菜单
        menuService.getListByUser(user.getType(), user.getTenantCode(), user.getId(), UcConst.MenuTypeEnum.MENU.value(), 1)
                .forEach(menu -> menuList.add(new TreeNode<>(menu.getId(), menu.getPid(), menu.getName(), menu.getSort()).setExtra(Dict.create().set("icon", menu.getIcon()).set("url", menu.getUrl()).set("urlNewBlank", menu.getUrlNewBlank()))));
        List<Tree<Long>> menuTree = TreeNodeUtils.buildIdTree(menuList);
        return new Result<>().success(menuTree);
    }

    @PostMapping("userPermissions")
    @Operation(summary = "用户授权编码", description = "用户具备的权限,可用于按钮等的控制")
    @ApiOperationSupport(order = 240)
    public Result<?> userPermissions() {
        ShiroUser user = ShiroUtils.getUser();
        List<String> set = userService.getUserPermissions(user);

        return new Result<>().success(set);
    }

    @PostMapping("userRoleIds")
    @Operation(summary = "用户角色id", description = "用户具备的角色,可用于按钮等的控制")
    @ApiOperationSupport(order = 250)
    public Result<?> userRoles() {
        ShiroUser user = ShiroUtils.getUser();
        List<Long> set = userService.getUserRoleIds(user);

        return new Result<>().success(set);
    }

    @PostMapping("userRoleCodes")
    @Operation(summary = "用户角色编码", description = "用户具备的角色,可用于按钮等的控制")
    @ApiOperationSupport(order = 250)
    public Result<?> userRoleCodes() {
        ShiroUser user = ShiroUtils.getUser();
        List<String> set = userService.getUserRoleCodes(user);

        return new Result<>().success(set);
    }

}
