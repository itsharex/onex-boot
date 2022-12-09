package com.nb6868.onex.sys.mail;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.dingtalk.BaseResponse;
import com.nb6868.onex.common.dingtalk.DingTalkApi;
import com.nb6868.onex.common.msg.MsgSendForm;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.sys.MsgConst;
import com.nb6868.onex.sys.entity.MsgLogEntity;
import com.nb6868.onex.sys.entity.MsgTplEntity;
import com.nb6868.onex.sys.service.MsgLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 钉钉工作通知消息
 * see {<a href="https://open.dingtalk.com/document/orgapp-server/asynchronous-sending-of-enterprise-session-messages">...</a>}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
@Service("DingtalkNotifyMailService")
public class DingtalkNotifyMailService extends AbstractMailService {

    @Override
    public boolean sendMail(MsgTplEntity mailTpl, MsgSendForm request) {
        AssertUtils.isTrue(null == mailTpl.getParams() || StrUtil.hasBlank(
                mailTpl.getParams().getStr("AppKeyId"),
                mailTpl.getParams().getStr("AppKeySecret")
        ), "请检查消息模板参数配置");

        JSONObject params = request.getContentParams().set("agent_id", mailTpl.getParams().getStr("AgentId"));
        // 保存记录
        MsgLogService mailLogService = SpringUtil.getBean(MsgLogService.class);
        MsgLogEntity mailLog = new MsgLogEntity();
        mailLog.setTenantCode(mailTpl.getTenantCode());
        mailLog.setTplCode(mailTpl.getCode());
        mailLog.setMailFrom("dingtalk_notify");
        mailLog.setMailTo(request.getMailTo());
        mailLog.setContentParams(params);
        mailLog.setConsumeState(Const.BooleanEnum.FALSE.value());
        mailLog.setState(MsgConst.MailSendStateEnum.SENDING.value());
        // 设置有效时间
        int validTimeLimit = mailTpl.getParams().getInt("validTimeLimit", 0);
        mailLog.setValidEndTime(validTimeLimit <= 0 ? DateUtil.offsetMonth(DateUtil.date(), 99 * 12) : DateUtil.offsetSecond(DateUtil.date(), validTimeLimit));
        mailLogService.save(mailLog);

        BaseResponse sendResponse = DingTalkApi.sendNotifyMsg(mailTpl.getParams().getStr("AppKeyId"), mailTpl.getParams().getStr("AppKeySecret"), params);
        mailLog.setState(sendResponse.isSuccess() ? MsgConst.MailSendStateEnum.SUCCESS.value() : MsgConst.MailSendStateEnum.FAIL.value());
        mailLog.setResult(sendResponse.toString());
        mailLogService.updateById(mailLog);

        return sendResponse.isSuccess();
    }

}
