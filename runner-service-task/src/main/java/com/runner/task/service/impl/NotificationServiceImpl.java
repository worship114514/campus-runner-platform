package com.runner.task.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.runner.pojo.Notification;
import com.runner.task.mapper.NotificationMapper;
import com.runner.task.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    @Transactional
    public void createNotification(String userId, String type, String title, String content) {
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setStatus(0); // 0:未读
        notification.setCreatedTime(new Date());
        notificationMapper.insert(notification);
    }

    @Override
    public List<Notification> getUserNotifications(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Notification condition = new Notification();
        condition.setUserId(userId);
        // 按创建时间倒序
        return notificationMapper.select(condition);
    }

    @Override
    public Integer getUnreadCount(String userId) {
        Notification condition = new Notification();
        condition.setUserId(userId);
        condition.setStatus(0);
        List<Notification> list = notificationMapper.select(condition);
        return list != null ? list.size() : 0;
    }

    @Override
    @Transactional
    public void markAsRead(String notificationId, String userId) {
        Notification notification = notificationMapper.selectByPrimaryKey(notificationId);
        if (notification != null && notification.getUserId().equals(userId)) {
            notification.setStatus(1);
            notification.setReadTime(new Date());
            notificationMapper.updateByPrimaryKeySelective(notification);
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(String userId) {
        Notification condition = new Notification();
        condition.setUserId(userId);
        condition.setStatus(0);
        List<Notification> list = notificationMapper.select(condition);
        if (list != null && !list.isEmpty()) {
            for (Notification notification : list) {
                notification.setStatus(1);
                notification.setReadTime(new Date());
                notificationMapper.updateByPrimaryKeySelective(notification);
            }
        }
    }
}