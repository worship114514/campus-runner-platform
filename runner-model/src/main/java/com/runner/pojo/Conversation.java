package com.runner.pojo;

import javax.persistence.*;
import java.util.Date;

@Table(name = "conversation")
public class Conversation {
    @Id
    private String id;

    @Column(name = "task_id")
    private String taskId;

    @Column(name = "user_a_id")
    private String userAId;

    @Column(name = "user_a_name")
    private String userAName;

    @Column(name = "user_a_face")
    private String userAFace;

    @Column(name = "user_b_id")
    private String userBId;

    @Column(name = "user_b_name")
    private String userBName;

    @Column(name = "user_b_face")
    private String userBFace;

    @Column(name = "last_message")
    private String lastMessage;

    @Column(name = "last_time")
    private Date lastTime;

    @Column(name = "user_a_unread")
    private Integer userAUnread;

    @Column(name = "user_b_unread")
    private Integer userBUnread;

    @Column(name = "user_a_deleted")
    private Integer userADeleted;

    @Column(name = "user_b_deleted")
    private Integer userBDeleted;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "updated_time")
    private Date updatedTime;

    // getter/setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getUserAId() { return userAId; }
    public void setUserAId(String userAId) { this.userAId = userAId; }

    public String getUserAName() { return userAName; }
    public void setUserAName(String userAName) { this.userAName = userAName; }

    public String getUserAFace() { return userAFace; }
    public void setUserAFace(String userAFace) { this.userAFace = userAFace; }

    public String getUserBId() { return userBId; }
    public void setUserBId(String userBId) { this.userBId = userBId; }

    public String getUserBName() { return userBName; }
    public void setUserBName(String userBName) { this.userBName = userBName; }

    public String getUserBFace() { return userBFace; }
    public void setUserBFace(String userBFace) { this.userBFace = userBFace; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public Date getLastTime() { return lastTime; }
    public void setLastTime(Date lastTime) { this.lastTime = lastTime; }

    public Integer getUserAUnread() { return userAUnread; }
    public void setUserAUnread(Integer userAUnread) { this.userAUnread = userAUnread; }

    public Integer getUserBUnread() { return userBUnread; }
    public void setUserBUnread(Integer userBUnread) { this.userBUnread = userBUnread; }

    public Integer getUserADeleted() { return userADeleted; }
    public void setUserADeleted(Integer userADeleted) { this.userADeleted = userADeleted; }

    public Integer getUserBDeleted() { return userBDeleted; }
    public void setUserBDeleted(Integer userBDeleted) { this.userBDeleted = userBDeleted; }

    public Date getCreatedTime() { return createdTime; }
    public void setCreatedTime(Date createdTime) { this.createdTime = createdTime; }

    public Date getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(Date updatedTime) { this.updatedTime = updatedTime; }
}