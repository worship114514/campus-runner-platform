package com.runner.pojo;

import javax.persistence.*;
import java.util.Date;

@Table(name = "message")
public class Message {
    @Id
    private String id;

    @Column(name = "conversation_id")
    private String conversationId;

    @Column(name = "from_user_id")
    private String fromUserId;

    @Column(name = "from_user_name")
    private String fromUserName;

    @Column(name = "from_user_face")
    private String fromUserFace;

    @Column(name = "to_user_id")
    private String toUserId;

    private String content;

    private Integer status;

    @Column(name = "is_recalled")
    private Integer isRecalled;

    @Column(name = "recall_time")
    private Date recallTime;

    @Column(name = "created_time")
    private Date createdTime;

    // getter/setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public String getFromUserId() { return fromUserId; }
    public void setFromUserId(String fromUserId) { this.fromUserId = fromUserId; }

    public String getFromUserName() { return fromUserName; }
    public void setFromUserName(String fromUserName) { this.fromUserName = fromUserName; }

    public String getFromUserFace() { return fromUserFace; }
    public void setFromUserFace(String fromUserFace) { this.fromUserFace = fromUserFace; }

    public String getToUserId() { return toUserId; }
    public void setToUserId(String toUserId) { this.toUserId = toUserId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getIsRecalled() { return isRecalled; }
    public void setIsRecalled(Integer isRecalled) { this.isRecalled = isRecalled; }

    public Date getRecallTime() { return recallTime; }
    public void setRecallTime(Date recallTime) { this.recallTime = recallTime; }

    public Date getCreatedTime() { return createdTime; }
    public void setCreatedTime(Date createdTime) { this.createdTime = createdTime; }
}