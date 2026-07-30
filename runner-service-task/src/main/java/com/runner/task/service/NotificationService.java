package com.runner.task.service;

import com.runner.pojo.Notification;
import java.util.List;

public interface NotificationService {

    /**
     * 创建通知
     */
    void createNotification(String userId, String type, String title, String content);

    /**
     * 获取用户通知列表
     */
    List<Notification> getUserNotifications(String userId, Integer page, Integer pageSize);

    /**
     * 获取用户未读通知数量
     */
    Integer getUnreadCount(String userId);

    /**
     * 标记通知为已读
     */
    void markAsRead(String notificationId, String userId);

    /**
     * 标记所有通知为已读
     */
    void markAllAsRead(String userId);
}