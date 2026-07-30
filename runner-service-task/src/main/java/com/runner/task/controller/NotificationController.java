package com.runner.task.controller;

import com.runner.grace.result.GraceJSONResult;
import com.runner.pojo.Notification;
import com.runner.task.service.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "通知管理")
@RestController
@RequestMapping("notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @ApiOperation("获取用户通知列表")
    @GetMapping("/list")
    public GraceJSONResult getNotifications(@RequestParam String userId,
                                            @RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "20") Integer pageSize) {
        List<Notification> list = notificationService.getUserNotifications(userId, page, pageSize);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("unreadCount", notificationService.getUnreadCount(userId));
        return GraceJSONResult.ok(result);
    }

    @ApiOperation("获取未读通知数量")
    @GetMapping("/unreadCount")
    public GraceJSONResult getUnreadCount(@RequestParam String userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("unreadCount", notificationService.getUnreadCount(userId));
        return GraceJSONResult.ok(result);
    }

    @ApiOperation("标记通知为已读")
    @PostMapping("/read")
    public GraceJSONResult markAsRead(@RequestParam String notificationId,
                                      @RequestParam String userId) {
        notificationService.markAsRead(notificationId, userId);
        return GraceJSONResult.ok();
    }

    @ApiOperation("标记所有通知为已读")
    @PostMapping("/readAll")
    public GraceJSONResult markAllAsRead(@RequestParam String userId) {
        notificationService.markAllAsRead(userId);
        return GraceJSONResult.ok();
    }
}