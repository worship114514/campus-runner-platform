package com.runner.task.service.impl;

import com.runner.pojo.Conversation;
import com.runner.pojo.Message;
import com.runner.task.mapper.ConversationMapper;
import com.runner.task.mapper.MessageMapper;
import com.runner.task.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private MessageMapper messageMapper;

    private static final long RECALL_TIME_LIMIT = 5 * 60 * 1000;

    @Override
    @Transactional
    public Conversation createConversation(String taskId,
                                           String userAId, String userAName, String userAFace,
                                           String userBId, String userBName, String userBFace) {
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
    public Conversation getConversation(String conversationId) {
        return conversationMapper.selectByPrimaryKey(conversationId);
    }

    @Override
    @Transactional
    public Message sendMessage(String conversationId, String fromUserId,
                               String fromUserName, String fromUserFace,
                               String toUserId, String content) {
        // 先查出会话，获取 userAId 和 userBId
        Conversation conversation = conversationMapper.selectByPrimaryKey(conversationId);
        if (conversation == null) {
            throw new RuntimeException("会话不存在");
        }

        String id = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        Message message = new Message();
        message.setId(id);
        message.setConversationId(conversationId);
        message.setFromUserId(fromUserId);
        message.setFromUserName(fromUserName);
        message.setFromUserFace(fromUserFace);
        message.setToUserId(toUserId);
        message.setContent(content);
        message.setStatus(0);
        message.setIsRecalled(0);
        message.setCreatedTime(new Date());

        messageMapper.insert(message);

        // 更新会话最后消息
        conversationMapper.updateLastMessage(conversationId, content, new Date());

        // 增加对方未读数（传入 userAId 和 userBId）
        conversationMapper.incrementUnread(
                conversationId,
                toUserId,
                conversation.getUserAId(),
                conversation.getUserBId()
        );

        return message;
    }

    @Override
    public List<Message> getMessages(String conversationId, String userId) {
        // 1. 标记该用户的所有消息为已读
        messageMapper.markAsRead(conversationId, userId);

        // 2. 清零未读（先查出会话，获取 userAId 和 userBId）
        Conversation conversation = conversationMapper.selectByPrimaryKey(conversationId);
        if (conversation != null) {
            conversationMapper.clearUnread(
                    conversationId,
                    userId,
                    conversation.getUserAId(),
                    conversation.getUserBId()
            );
        }

        return messageMapper.selectByConversationId(conversationId);
    }

    @Override
    @Transactional
    public void markAsRead(String conversationId, String userId) {
        messageMapper.markAsRead(conversationId, userId);

        Conversation conversation = conversationMapper.selectByPrimaryKey(conversationId);
        if (conversation != null) {
            conversationMapper.clearUnread(
                    conversationId,
                    userId,
                    conversation.getUserAId(),
                    conversation.getUserBId()
            );
        }
    }

    @Override
    @Transactional
    public boolean recallMessage(String messageId, String userId) {
        Message message = messageMapper.selectByPrimaryKey(messageId);
        if (message == null) {
            return false;
        }

        if (!message.getFromUserId().equals(userId)) {
            return false;
        }

        long diff = System.currentTimeMillis() - message.getCreatedTime().getTime();
        if (diff > RECALL_TIME_LIMIT) {
            return false;
        }

        if (message.getIsRecalled() == 1) {
            return false;
        }

        messageMapper.recallMessage(messageId, new Date());
        return true;
    }

    @Override
    public int getUnreadCount(String userId) {
        List<Message> messages = messageMapper.selectUnreadByUserId(userId);
        return messages != null ? messages.size() : 0;
    }

    @Override
    @Transactional
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