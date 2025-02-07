package com.nb6868.onex.websocket.controller;

import com.nb6868.onex.common.pojo.BaseReq;
import com.nb6868.onex.common.pojo.Result;
import com.nb6868.onex.common.validator.group.DefaultGroup;
import com.nb6868.onex.websocket.dto.WebSocketSendReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("SysWebSocket")
@RequestMapping("/sys/webSocket/")
@Validated
@Tag(name = "WebSocket")
public class WebSocketController {

    @Autowired
    WebSocketServer webSocketServer;

    @PostMapping("getOpenSockets")
    @Operation(summary = "获得目前连接的Socket")
    @RequiresPermissions(value = {"admin:super", "admin:websocket", "sys:websocket:query"}, logical = Logical.OR)
    public Result<?> getOpenSockets(@Validated @RequestBody BaseReq req) {
        List<String> sidList = webSocketServer.getSidList();
        return new Result<>().success(sidList);
    }

    @PostMapping("sendOneMessage")
    @Operation(summary = "发送单点消息")
    @RequiresPermissions(value = {"admin:super", "admin:websocket", "sys:websocket:send"}, logical = Logical.OR)
    public Result<?> sendOneMessage(@Validated(value = {DefaultGroup.class, WebSocketSendReq.SendOneGroup.class}) @RequestBody WebSocketSendReq req) {
        boolean result = webSocketServer.sendOneMessage(req.getSid(), req.getContent());
        return new Result<>().bool(result);
    }

    @PostMapping("sendMultiMessage")
    @Operation(summary = "发送批量消息")
    @RequiresPermissions(value = {"admin:super", "admin:websocket", "sys:websocket:send"}, logical = Logical.OR)
    public Result<?> sendMultiMessage(@Validated(value = {DefaultGroup.class, WebSocketSendReq.SendMultiGroup.class}) @RequestBody WebSocketSendReq req) {
        webSocketServer.sendMultiMessage(req.getSidList(), req.getContent());
        return new Result<>();
    }

    @PostMapping("sendAllMessage")
    @Operation(summary = "发送广播消息")
    @RequiresPermissions(value = {"admin:super", "admin:websocket", "sys:websocket:send"}, logical = Logical.OR)
    public Result<?> sendAllMessage(@Validated(value = {DefaultGroup.class}) @RequestBody WebSocketSendReq req) {
        webSocketServer.sendAllMessage(req.getContent());
        return new Result<>();
    }

}
