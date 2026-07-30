package com.runner.task.mapper;

import com.runner.my.mapper.MyMapper;
import com.runner.pojo.Message;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageMapper extends MyMapper<Message> {

    /**
     * 查询会话的所有消息（按时间正序）
     */
    List<Message> selectByConversationId(@Param("conversationId") String conversationId);

    /**
     * 将消息标记为已读
     */
    int markAsRead(@Param("conversationId") String conversationId,
                   @Param("userId") String userId);

    /**
     * 撤回消息
     */
    int recallMessage(@Param("messageId") String messageId,
                      @Param("recallTime") java.util.Date recallTime);

    /**
     * 查询未读消息列表
     */
    List<Message> selectUnreadByUserId(@Param("userId") String userId);
}