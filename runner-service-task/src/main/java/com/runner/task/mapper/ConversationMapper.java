package com.runner.task.mapper;

import com.runner.my.mapper.MyMapper;
import com.runner.pojo.Conversation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ConversationMapper extends MyMapper<Conversation> {

    Conversation selectByTaskId(@Param("taskId") String taskId);

    List<Conversation> selectByUserId(@Param("userId") String userId);

    int updateLastMessage(@Param("conversationId") String conversationId,
                          @Param("lastMessage") String lastMessage,
                          @Param("lastTime") Date lastTime);

    // 修复：增加 userAId 和 userBId 参数
    int incrementUnread(@Param("conversationId") String conversationId,
                        @Param("userId") String userId,
                        @Param("userAId") String userAId,
                        @Param("userBId") String userBId);

    // 修复：增加 userAId 和 userBId 参数
    int clearUnread(@Param("conversationId") String conversationId,
                    @Param("userId") String userId,
                    @Param("userAId") String userAId,
                    @Param("userBId") String userBId);
}