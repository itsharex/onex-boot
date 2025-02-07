package com.nb6868.onex.uc.controller;

/*
@Deprecated
@RestController
@RequestMapping("/uc/auth/qrcode/")
@AccessControl
@Validated
@Tag(name = "用户二维码登录")
@Slf4j
public class AuthQrcodeController {

    @Autowired
    private AuthProps authProps;
    @Autowired
    private QrcodeService qrcodeService;
    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private ParamsService paramsService;

    @PostMapping("create")
    @Operation(summary = "生成二维码", description = "Anon")
    public Result<?> create() {
        // 生成随机码
        String uuid = IdUtil.fastSimpleUUID();
        // 保存
        qrcodeService.createQrcode(uuid);
        return new Result<>();
    }

    @PostMapping("userLogin")
    @Operation(summary = "通过二维码登录", description = "Anon")
    public Result<?> userLogin(@Validated(value = {DefaultGroup.class}) @RequestBody CodeLoginForm form) {
        // 获得对应登录类型的登录参数
        JSONObject loginParams = paramsService.getSystemPropsJson(form.getType());
        AssertUtils.isNull(loginParams, "缺少[" + form.getType() + "]对应的登录配置");

        // 从缓存中，通过qrcode获得用户id
        String qrcodeContent = qrcodeService.getQrcode(form.getCode());
        if (StrUtil.isBlank(qrcodeContent)) {
            return new Result<>().error(10400, "二维码已过期,请刷新后重试");
        } else if ("scanned".equalsIgnoreCase(qrcodeContent)) {
            return new Result<>().error(10401, "二维码已扫码,请先确认登录");
        } else if ("none".equalsIgnoreCase(qrcodeContent)) {
            return new Result<>().error(10402, "请扫码,并在手机上确认登录");
        }
        // 获得用户
        long userId;
        try {
            userId = NumberUtil.parseLong(qrcodeContent);
        } catch (Exception e) {
            return new Result<>().error(10403, "二维码异常,请刷新后重试");
        }
        // 用完删掉
        qrcodeService.removeQrcode(form.getCode());
        // 获得用户,判断用户
        UserEntity user = userService.getById(userId);
        AssertUtils.isNull(user, ErrorCode.ACCOUNT_NOT_EXIST);
        // 判断用户状态
        AssertUtils.isFalse(user.getState() == UcConst.UserStateEnum.ENABLED.getCode(), ErrorCode.ACCOUNT_DISABLE);

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

    @PostMapping("scan")
    @AccessControl
    @Operation(summary = "移动端扫描二维码", description = "Anon")
    public Result<?> scan(@Validated(value = {DefaultGroup.class}) @RequestBody CodeForm form) {
        // 从缓存中，通过qrcode获得用户id
        String qrcodeContent = qrcodeService.getQrcode(form.getCode());
        if (StrUtil.isBlank(qrcodeContent)) {
            return new Result<>().error(10400, "二维码已过期,请刷新后重试");
        } else if ("scanned".equalsIgnoreCase(qrcodeContent)) {
            // 允许重复扫描
            return new Result<>();
        } else if ("none".equalsIgnoreCase(qrcodeContent)) {
            // 更新缓存为scanned(已扫码)
            qrcodeService.updateQrcode(form.getCode(), "scanned");
            return new Result<>();
        } else {
            return new Result<>().error("二维码已扫描,请刷新后重试");
        }
    }

}*/
