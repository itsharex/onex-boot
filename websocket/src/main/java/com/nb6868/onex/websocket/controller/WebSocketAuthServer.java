
package com.nb6868.onex.websocket.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import com.nb6868.onex.common.shiro.ShiroDao;
import com.nb6868.onex.common.util.JwtUtils;
import com.nb6868.onex.websocket.config.WebSocketConfig;
import com.nb6868.onex.websocket.dto.SidDTO;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocketAuthServer
 * 带有用户信息验证的Server，注意单用户只能发起一个连接
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "onex.websocket.authserver", havingValue = "true")
@ServerEndpoint(value = "/wsauth/{uid}" ,configurator = WebSocketConfig.class)
public class WebSocketAuthServer {

    @Value("${onex.websocket.max-idle-timeout:3600000}")
    private long maxIdleTimeout;
    @Value("${onex.shiro.type:jwt}")
    private String shiroType;
    @Autowired
    ShiroDao shiroDao;

    // 线程安全Set，存放每个客户端对应的MyWebSocket对象
    private final static Map<String, WebSocketAuthServer> webSockets = new ConcurrentHashMap<>();
    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    /**
     * 当前连接session id
     */
    public List<SidDTO> getSidList() {
        List<SidDTO> sidList = new ArrayList<>();
        webSockets.forEach((sid, webSocketServer) -> {
            SidDTO dto = new SidDTO();
            dto.setSid(sid);
            sidList.add(dto);
        });
        return sidList;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(@PathParam(value = "uid") String uid, Session session) {
        // 校验token,并保存人和sid的关系
        Long userId = null;
        String token = MapUtil.getStr(session.getUserProperties(), "token");
        if (StrUtil.isNotBlank(token)) {
            if (StrUtil.equalsIgnoreCase(shiroType, "jwt")) {
                JWT jwt = JwtUtils.parseToken(token);
                if (ObjUtil.isNotNull(jwt) && ObjUtil.isNotNull(jwt.getPayload()) && ObjUtil.isNotNull(jwt.getPayload().getClaimsJson())) {
                    userId = NumberUtil.parseLong(jwt.getPayload().getClaimsJson().getStr("id"));
                }
            } else if (StrUtil.equalsIgnoreCase(shiroType, "uuid")) {
                // 只校验了token，没校验人的有效
                Map<String, Object> tokenEntity = shiroDao.getUserTokenByToken(token);
                userId = MapUtil.getLong(tokenEntity, "user_id", null);
            }
        }
        if (null == userId || 0L == userId || !StrUtil.equalsIgnoreCase(String.valueOf(userId), uid)) {
            // token校验失败
            try {
                session.close();
            } catch (IOException e) {
                log.error("session关闭异常", e);
            }
            return;
        }
        // 校验成功后，保存人和sid的关系，偷懒拼接起来，可按照实际情况重新处理
        session.setMaxIdleTimeout(maxIdleTimeout);
        this.session = session;
        webSockets.put(uid, this);
        log.info("[websocket], OnOpen, sid={}, userId={}, total={}", uid, userId, webSockets.size());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam(value = "uid") String uid) {
        webSockets.remove(uid);
        log.info("[websocket], OnClose, total={}", webSockets.size());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param uid uid
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(@PathParam(value = "uid") String uid, String message) {
        log.info("[websocket], OnMessage, uid={}, message={}", uid, message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("[websocket], OnError", error);
    }

    /**
     * 发送广播消息
     */
    public void sendAllMessage(String message) {
        log.info("[websocket] sendAllMessage, message={}", message);
        webSockets.forEach((sid, webSocketServer) -> {
            try {
                webSocketServer.session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 发送单点消息
     */
    public boolean sendOneMessage(String uid, String message) {
        try {
            webSockets.get(uid).session.getAsyncRemote().sendText(message);
            log.info("[websocket] sendOneMessage, uid={}, message={}, result={}", uid, message, "success");
            return true;
        } catch (Exception e) {
            log.error("[websocket] sendOneMessage, uid={}, message={}, result={}", uid, message, e.getMessage());
            return false;
        }
    }

    /**
     * 发送多点消息
     */
    public void sendMultiMessage(List<String> sidList, String message) {
        log.info("[websocket] sendMultiMessage, sidList={}, message={}", CollUtil.join(sidList, ","), message);
        sidList.forEach(sid -> sendOneMessage(sid, message));
    }

}
