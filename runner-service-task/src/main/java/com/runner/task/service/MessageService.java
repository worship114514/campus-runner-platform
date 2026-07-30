package com.runner.task.service;

import com.runner.pojo.Conversation;
import com.runner.pojo.Message;

import java.util.List;

public interface MessageService {

    /**
     * 创建会话（任务被接单时调用）
     */
    Conversation createConversation(String taskId,
                                    String userAId, String userAName, String userAFace,
                                    String userBId, String userBName, String userBFace);

    /**
     * 获取用户的所有会话列表
     */
    List<Conversation> getConversationList(String userId);

    /**
     * 获取会话详情（含对方用户信息）
     */
    Conversation getConversation(String conversationId);

    /**
     * 发送消息
     */
    Message sendMessage(String conversationId, String fromUserId,
                        String fromUserName, String fromUserFace,
                        String toUserId, String content);

    /**
     * 获取会话的所有消息
     */
    List<Message> getMessages(String conversationId, String userId);

    /**
     * 标记消息为已读
     */
    void markAsRead(String conversationId, String userId);

    /**
     * 撤回消息（5分钟内）
     */
    boolean recallMessage(String messageId, String userId);

    /**
     * 获取用户未读消息数
     */
    int getUnreadCount(String userId);

    /**
     * 删除会话
     */
    void deleteConversation(String conversationId, String userId);
}