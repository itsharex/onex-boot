package com.nb6868.onex.msg;

import cn.hutool.core.util.ObjUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * 消息模块常量
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public interface MsgConst {

    /**
     /**
     * 验证码短信模板前缀
     */
    String SMS_CODE_TPL_PREFIX = "CODE_";

    /**
     * 短信验证码模板-通用
     */
    String SMS_TPL_CODE_COMMON = SMS_CODE_TPL_PREFIX + "COMMON";
    /**
     * 短信验证码模板-登录
     */
    String SMS_TPL_LOGIN = SMS_CODE_TPL_PREFIX + "LOGIN";
    /**
     * 短信验证码模板-修改密码
     */
    String SMS_TPL_CHANGE_PASSWORD = SMS_CODE_TPL_PREFIX + "CHANGE_PASSWORD";
    /**
     * 短信验证码模板-注册
     */
    String SMS_TPL_REGISTER = SMS_CODE_TPL_PREFIX+  "REGISTER";
    /**
     * 模板参数配置错误
     */
    String MAIL_TPL_PARAMS_ERROR = "请检查消息模板参数配置";

    /**
     * 消息渠道类型
     */
    @Getter
    @AllArgsConstructor
    enum MailChannelEnum {

        /**
         * 消息渠道类型
         */
        SMS("短信"),
        EMAIL("电子邮件"),
        DINGTALK("钉钉"),
        WX_MP_TEMPLATE("微信公众号模板消息"),
        WX_MA_SUBSCRIBE("微信小程序订阅消息");

        private String code;

    }

    /**
     * 消息类型
     */
    @Getter
    @AllArgsConstructor
    enum MailTypeEnum {

        /**
         * 支持的消息类型定义
         */
        CODE(1, "验证码"),
        NOTIFY(2, "通知"),
        ADV(3, "营销广告");

        private int code;
        private String title;

        public static MailTypeEnum findByCode(Integer codeValue) {
            return Stream.of(values())
                    .filter(p -> ObjUtil.equal(codeValue, p.getCode()))
                    .findFirst()
                    .orElse(null);
        }

        public static String getTitleByCode(Integer codeValue) {
            MailTypeEnum r = findByCode(codeValue);
            return ObjUtil.isNull(r) ? "未定义" + codeValue : r.getTitle();
        }

        public static boolean isValid(Integer codeValue) {
            return findByCode(codeValue) != null;
        }
    }

    /**
     * 消息发送状态
     */
    @Getter
    @AllArgsConstructor
    enum MailSendStateEnum {

        /**
         * 支持的消息类型定义
         */
        SENDING(0, "发送中"),
        SUCCESS(1, "发送调用成功"),
        FAIL(-1, "发送调用失败"),
        CALLBACK_SUCCESS(10, "发送回调成功"),
        CALLBACK_FAIL(-10, "发送回调失败");

        private int code;
        private String title;

        public static MailSendStateEnum findByCode(Integer codeValue) {
            return Stream.of(values())
                    .filter(p -> ObjUtil.equal(codeValue, p.getCode()))
                    .findFirst()
                    .orElse(null);
        }

        public static String getTitleByCode(Integer codeValue) {
            MailSendStateEnum r = findByCode(codeValue);
            return ObjUtil.isNull(r) ? "未定义" + codeValue : r.getTitle();
        }

        public static boolean isValid(Integer codeValue) {
            return findByCode(codeValue) != null;
        }
    }

}
