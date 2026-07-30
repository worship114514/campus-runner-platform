package com.runner.task.service;

import com.runner.pojo.Conversation;

import java.util.List;

public interface ConversationService {

    /**
     * 根据任务ID查询会话
     */
    Conversation getByTaskId(String taskId);

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
     * 根据ID获取会话
     */
    Conversation getById(String conversationId);

    /**
     * 删除会话
     */
    void deleteConversation(String conversationId, String userId);
}