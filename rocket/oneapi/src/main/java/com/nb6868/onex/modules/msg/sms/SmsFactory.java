package com.nb6868.onex.modules.msg.sms;

import com.nb6868.onex.booster.exception.OnexException;

/**
 * 短信Factory
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class SmsFactory {

    public static AbstractSmsService build(String platform) {
        // 获取短信配置信息
        if ("aliyun".equalsIgnoreCase(platform)) {
            return new AliyunSmsService();
        } else if ("juhe".equalsIgnoreCase(platform)) {
            return new JuheSmsService();
        } else {
            throw new OnexException("未定义的短信发送平台");
        }
    }
}
