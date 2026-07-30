package com.runner.task.controller;

import com.runner.grace.result.GraceJSONResult;
import com.runner.pojo.Conversation;
import com.runner.pojo.Message;
import com.runner.task.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "消息管理")
@RestController
@RequestMapping("message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @ApiOperation("获取会话列表")
    @GetMapping("/conversations")
    public GraceJSONResult getConversations(@RequestParam String userId) {
        List<Conversation> list = messageService.getConversationList(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("unreadCount", messageService.getUnreadCount(userId));
        return GraceJSONResult.ok(result);
    }

    @ApiOperation("获取会话详情")
    @GetMapping("/conversation")
    public GraceJSONResult getConversation(@RequestParam String conversationId) {
        Conversation conversation = messageService.getConversation(conversationId);
        return GraceJSONResult.ok(conversation);
    }

    @ApiOperation("获取消息列表")
    @GetMapping("/messages")
    public GraceJSONResult getMessages(@RequestParam String conversationId,
                                       @RequestParam String userId) {
        List<Message> messages = messageService.getMessages(conversationId, userId);
        return GraceJSONResult.ok(messages);
    }

    @ApiOperation("发送消息")
    @PostMapping("/send")
    public GraceJSONResult sendMessage(@RequestParam String conversationId,
                                       @RequestParam String fromUserId,
                                       @RequestParam String fromUserName,
                                       @RequestParam(required = false) String fromUserFace,
                                       @RequestParam String toUserId,
                                       @RequestParam String content) {
        if (content == null || content.trim().isEmpty()) {
            return GraceJSONResult.errorMsg("消息内容不能为空");
        }
        if (content.length() > 500) {
            return GraceJSONResult.errorMsg("消息内容不能超过500字");
        }

        Message message = messageService.sendMessage(
                conversationId, fromUserId, fromUserName, fromUserFace,
                toUserId, content);
        return GraceJSONResult.ok(message);
    }

    @ApiOperation("标记已读")
    @PostMapping("/read")
    public GraceJSONResult markAsRead(@RequestParam String conversationId,
                                      @RequestParam String userId) {
        messageService.markAsRead(conversationId, userId);
        return GraceJSONResult.ok();
    }

    @ApiOperation("撤回消息")
    @PostMapping("/recall")
    public GraceJSONResult recallMessage(@RequestParam String messageId,
                                         @RequestParam String userId) {
        boolean result = messageService.recallMessage(messageId, userId);
        if (result) {
            return GraceJSONResult.ok("撤回成功");
        } else {
            return GraceJSONResult.errorMsg("撤回失败，消息可能已超过5分钟或不是您的消息");
        }
    }

    @ApiOperation("删除会话")
    @DeleteMapping("/conversation")
    public GraceJSONResult deleteConversation(@RequestParam String conversationId,
                                              @RequestParam String userId) {
        messageService.deleteConversation(conversationId, userId);
        return GraceJSONResult.ok();
    }

    @ApiOperation("获取未读消息数")
    @GetMapping("/unread")
    public GraceJSONResult getUnreadCount(@RequestParam String userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("unreadCount", messageService.getUnreadCount(userId));
        return GraceJSONResult.ok(result);
    }
}