package com.runner.task.service.impl;

import com.runner.pojo.Conversation;
import com.runner.task.mapper.ConversationMapper;
import com.runner.task.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ConversationServiceImpl implements ConversationService {

    @Autowired
    private ConversationMapper conversationMapper;

    @Override
    public Conversation getByTaskId(String taskId) {
        return conversationMapper.selectByTaskId(taskId);
    }

    @Override
    public Conversation createConversation(String taskId,
                                           String userAId, String userAName, String userAFace,
                                           String userBId, String userBName, String userBFace) {
        // 检查是否已存在
        Conversation existing = conversationMapper.selectByTaskId(taskId);
        if (existing != null) {
            return existing;
        }

        String id = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        Conversation conversation = new Conversation();
        conversation.setId(id);
        conversation.setTaskId(taskId);
        conversation.setUserAId(userAId);
        conversation.setUserAName(userAName);
        conversation.setUserAFace(userAFace);
        conversation.setUserBId(userBId);
        conversation.setUserBName(userBName);
        conversation.setUserBFace(userBFace);
        conversation.setUserAUnread(0);
        conversation.setUserBUnread(0);
        conversation.setUserADeleted(0);
        conversation.setUserBDeleted(0);
        conversation.setCreatedTime(new Date());
        conversation.setUpdatedTime(new Date());

        conversationMapper.insert(conversation);
        return conversation;
    }

    @Override
    public List<Conversation> getConversationList(String userId) {
        return conversationMapper.selectByUserId(userId);
    }

    @Override
    public Conversation getById(String conversationId) {
        return conversationMapper.selectByPrimaryKey(conversationId);
    }

    @Override
    public void deleteConversation(String conversationId, String userId) {
        Conversation conversation = conversationMapper.selectByPrimaryKey(conversationId);
        if (conversation == null) {
            return;
        }

        if (conversation.getUserAId().equals(userId)) {
            conversation.setUserADeleted(1);
        } else if (conversation.getUserBId().equals(userId)) {
            conversation.setUserBDeleted(1);
        } else {
            return;
        }
        conversation.setUpdatedTime(new Date());
        conversationMapper.updateByPrimaryKeySelective(conversation);
    }
}