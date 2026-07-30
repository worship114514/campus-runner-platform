package com.runner.task.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.runner.enums.TaskStatus;
import com.runner.exception.GraceException;
import com.runner.grace.result.GraceJSONResult;
import com.runner.grace.result.ResponseStatusEnum;
import com.runner.pojo.*;
import com.runner.pojo.bo.NewTaskBO;
import com.runner.task.mapper.EvaluationMapper;
import com.runner.task.mapper.OrderMapper;
import com.runner.task.mapper.TaskMapper;
import com.runner.task.service.ConversationService;
import com.runner.task.service.MessageService;
import com.runner.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Api(tags = "任务管理")
@RestController
@RequestMapping("task")
public class TaskController {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private EvaluationMapper evaluationMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisOperator redis;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ConversationService conversationService;

    @ApiOperation("发布任务")
    @PostMapping("/publish")
    public GraceJSONResult publishTask(@RequestParam String publisherId,
                                       @RequestParam String publisherName,
                                       @RequestParam(required = false) String publisherAvatar,
                                       @RequestBody @Valid NewTaskBO newTaskBO) {

        // 检查余额（调用钱包服务查询）
        String balanceUrl = "http://wallet.runner.gzmu.com:8007/wallet/balance?userId=" + publisherId;
        try {
            ResponseEntity<GraceJSONResult> balanceResp = restTemplate.getForEntity(balanceUrl, GraceJSONResult.class);
            if (balanceResp.getBody() != null && balanceResp.getBody().getStatus() == 200) {
                Map<String, Object> balanceData = (Map<String, Object>) balanceResp.getBody().getData();
                BigDecimal balance = new BigDecimal(balanceData.get("balance").toString());
                if (balance.compareTo(newTaskBO.getRewardAmount()) < 0) {
                    GraceException.display(ResponseStatusEnum.INSUFFICIENT_BALANCE);
                }
            }
        } catch (Exception e) {
            GraceException.display(ResponseStatusEnum.INSUFFICIENT_BALANCE);
        }

        // 创建任务
        Task task = new Task();
        BeanUtils.copyProperties(newTaskBO, task);
        task.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        task.setPublisherId(publisherId);
        task.setPublisherName(publisherName);
        task.setPublisherAvatar(publisherAvatar);
        task.setStatus(TaskStatus.PENDING.type);
        task.setCreatedTime(new Date());
        task.setUpdatedTime(new Date());
        taskMapper.insert(task);

        // 扣除发布者余额
        try {
            String deductUrl = "http://wallet.runner.gzmu.com:8007/wallet/deduct"
                    + "?userId=" + publisherId
                    + "&amount=" + newTaskBO.getRewardAmount()
                    + "&description=发布任务：" + newTaskBO.getTitle();
            restTemplate.postForEntity(deductUrl, null, GraceJSONResult.class);
            System.out.println("扣款成功: " + publisherId + ", 金额: " + newTaskBO.getRewardAmount());
        } catch (Exception e) {
            System.err.println("扣款失败: " + e.getMessage());
            // 扣款失败，删除任务
            taskMapper.deleteByPrimaryKey(task.getId());
            GraceException.display(ResponseStatusEnum.INSUFFICIENT_BALANCE);
        }

        return GraceJSONResult.ok(task);
    }

    @ApiOperation("接单")
    @PostMapping("/accept")
    public GraceJSONResult acceptTask(@RequestParam String taskId,
                                      @RequestParam String runnerId,
                                      @RequestParam String runnerName,
                                      @RequestParam(required = false) String runnerAvatar) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        if (task == null) {
            GraceException.display(ResponseStatusEnum.TASK_NOT_FOUND);
        }
        if (task.getStatus() != TaskStatus.PENDING.type) {
            GraceException.display(ResponseStatusEnum.TASK_ALREADY_TAKEN);
        }

        task.setRunnerId(runnerId);
        task.setRunnerName(runnerName);
        task.setRunnerAvatar(runnerAvatar);
        task.setStatus(TaskStatus.ACCEPTED.type);
        task.setUpdatedTime(new Date());
        taskMapper.updateByPrimaryKeySelective(task);

        // 创建订单
        Order order = new Order();
        order.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        order.setOrderNo(System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8));
        order.setTaskId(taskId);
        order.setPublisherId(task.getPublisherId());
        order.setRunnerId(runnerId);
        order.setRewardAmount(task.getRewardAmount());
        order.setStatus(TaskStatus.ACCEPTED.type);
        order.setCreatedTime(new Date());
        order.setUpdatedTime(new Date());
        orderMapper.insert(order);

        // 创建会话（使用 task 中的信息）
        String publisherName = task.getPublisherName() != null ? task.getPublisherName() : "用户";
        String publisherAvatar = task.getPublisherAvatar() != null ? task.getPublisherAvatar() : "";
        try {
            conversationService.createConversation(
                    taskId,
                    task.getPublisherId(), publisherName, publisherAvatar,
                    runnerId, runnerName, runnerAvatar != null ? runnerAvatar : ""
            );
        } catch (Exception e) {
            System.err.println("创建会话失败: " + e.getMessage());
        }

        // 发送通知
        createNotification(task.getPublisherId(), "TASK_ACCEPTED", "任务已被接单",
                "您的任务《" + task.getTitle() + "》已被 " + runnerName + " 接单");

        sendSystemMessage(task.getPublisherId(),
                "您的任务《" + task.getTitle() + "》已被 " + runnerName + " 接单");

        return GraceJSONResult.ok(task);
    }

    @ApiOperation("标记送达")
    @PostMapping("/deliver")
    public GraceJSONResult deliverTask(@RequestParam String taskId,
                                       @RequestParam(required = false) String deliveryPhoto) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        if (task == null) {
            GraceException.display(ResponseStatusEnum.TASK_NOT_FOUND);
        }
        if (task.getStatus() != TaskStatus.ACCEPTED.type) {
            GraceException.display(ResponseStatusEnum.TASK_STATUS_ERROR);
        }

        // 如果有送达照片则保存，没有则跳过
        if (StringUtils.isNotBlank(deliveryPhoto)) {
            task.setDeliveryPhoto(deliveryPhoto);
        }
        task.setStatus(TaskStatus.DELIVERED.type);
        task.setUpdatedTime(new Date());
        taskMapper.updateByPrimaryKeySelective(task);

        // 更新订单状态
        Order condition = new Order();
        condition.setTaskId(taskId);
        List<Order> orders = orderMapper.select(condition);
        if (orders != null && !orders.isEmpty()) {
            Order order = orders.get(0);
            order.setStatus(TaskStatus.DELIVERED.type);
            order.setUpdatedTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }

        // 发送通知
        createNotification(task.getPublisherId(), "TASK_DELIVERED", "任务已送达",
                "您的任务《" + task.getTitle() + "》已送达，请确认完成");

        sendSystemMessage(task.getPublisherId(),
                "您的任务《" + task.getTitle() + "》已送达，请确认完成");

        return GraceJSONResult.ok();
    }

    @ApiOperation("确认完成")
    @PostMapping("/complete")
    public GraceJSONResult completeTask(@RequestParam String taskId,
                                        @RequestParam String publisherId) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        if (task == null) {
            GraceException.display(ResponseStatusEnum.TASK_NOT_FOUND);
        }
        if (task.getStatus() != TaskStatus.DELIVERED.type) {
            GraceException.display(ResponseStatusEnum.TASK_STATUS_ERROR);
        }
        if (!task.getPublisherId().equals(publisherId)) {
            GraceException.display(ResponseStatusEnum.NO_AUTH);
        }

        task.setStatus(TaskStatus.COMPLETED.type);
        task.setUpdatedTime(new Date());
        taskMapper.updateByPrimaryKeySelective(task);

        // 更新订单状态
        Order condition = new Order();
        condition.setTaskId(taskId);
        List<Order> orders = orderMapper.select(condition);
        if (orders != null && !orders.isEmpty()) {
            Order order = orders.get(0);
            order.setStatus(TaskStatus.COMPLETED.type);
            order.setCompletedTime(new Date());
            order.setUpdatedTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }

        // ===== 发放酬劳给跑腿员 =====
        if (task.getRunnerId() != null && task.getRewardAmount() != null) {
            try {
                String walletUrl = "http://wallet.runner.gzmu.com:8007/wallet/addIncome"
                        + "?userId=" + task.getRunnerId()
                        + "&amount=" + task.getRewardAmount()
                        + "&description=任务完成收入：" + task.getTitle();

                restTemplate.postForEntity(walletUrl, null, GraceJSONResult.class);
                System.out.println("跑腿员收入添加成功: " + task.getRunnerId() + ", 金额: " + task.getRewardAmount());
            } catch (Exception e) {
                System.err.println("添加跑腿员收入失败: " + e.getMessage());
                // 不阻塞主流程，记录日志
            }
        }

        // 发送通知
        createNotification(task.getRunnerId(), "TASK_COMPLETED", "任务已完成",
                "您完成的任务《" + task.getTitle() + "》已确认完成，酬劳已到账");

        if (task.getRunnerId() != null) {
            sendSystemMessage(task.getRunnerId(),
                    "您完成的任务《" + task.getTitle() + "》已确认完成，酬劳已到账");
        }

        return GraceJSONResult.ok();
    }

    @ApiOperation("取消任务")
    @PostMapping("/cancel")
    public GraceJSONResult cancelTask(@RequestParam String taskId,
                                      @RequestParam String userId) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        if (task == null) {
            GraceException.display(ResponseStatusEnum.TASK_NOT_FOUND);
        }
        if (task.getStatus() != TaskStatus.PENDING.type && task.getStatus() != TaskStatus.ACCEPTED.type) {
            GraceException.display(ResponseStatusEnum.TASK_STATUS_ERROR);
        }
        if (!task.getPublisherId().equals(userId) && !task.getRunnerId().equals(userId)) {
            GraceException.display(ResponseStatusEnum.NO_AUTH);
        }

        // 只有发布者取消任务时才退款
        boolean needRefund = task.getPublisherId().equals(userId);

        task.setStatus(TaskStatus.CANCELLED.type);
        task.setUpdatedTime(new Date());
        taskMapper.updateByPrimaryKeySelective(task);

        // 更新订单状态
        Order condition = new Order();
        condition.setTaskId(taskId);
        List<Order> orders = orderMapper.select(condition);
        if (orders != null && !orders.isEmpty()) {
            Order order = orders.get(0);
            order.setStatus(TaskStatus.CANCELLED.type);
            order.setUpdatedTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }

        // 退款
        if (needRefund && task.getRewardAmount() != null) {
            try {
                // 调用钱包服务退款
                String refundUrl = "http://wallet.runner.gzmu.com:8007/wallet/refund"
                        + "?userId=" + task.getPublisherId()
                        + "&amount=" + task.getRewardAmount()
                        + "&taskId=" + taskId
                        + "&reason=任务取消";

                restTemplate.postForEntity(refundUrl, null, GraceJSONResult.class);
                System.out.println("退款成功: 任务 " + taskId + "，金额 " + task.getRewardAmount());
            } catch (Exception e) {
                System.err.println("退款失败: " + e.getMessage());
                // 记录失败日志，后续人工处理
            }
        }

        // 发送通知
        createNotification(task.getPublisherId(), "TASK_CANCELLED", "任务已取消",
                "您的任务《" + task.getTitle() + "》已取消" + (needRefund ? "，已退款" : ""));

        if (userId.equals(task.getPublisherId()) && task.getRunnerId() != null) {
            sendSystemMessage(task.getRunnerId(),
                    "任务《" + task.getTitle() + "》已被发布者取消");
        } else if (userId.equals(task.getRunnerId()) && task.getPublisherId() != null) {
            sendSystemMessage(task.getPublisherId(),
                    "任务《" + task.getTitle() + "》已被跑腿员取消");
        }

        return GraceJSONResult.ok();
    }

    private void createNotification(String userId, String type, String title, String content) {
        try {
            String notificationUrl = "http://task.runner.gzmu.com:8001/notification/create"
                    + "?userId=" + userId
                    + "&type=" + type
                    + "&title=" + title
                    + "&content=" + content;

            restTemplate.postForEntity(notificationUrl, null, GraceJSONResult.class);
        } catch (Exception e) {
            System.err.println("发送通知失败: " + e.getMessage());
        }
    }

    @ApiOperation("查询任务大厅列表")
    @GetMapping("/list")
    public GraceJSONResult getTaskList(@RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       @RequestParam(required = false) Integer status,
                                       @RequestParam(required = false) String keyword) {
        PageHelper.startPage(page, pageSize);
        List<Task> taskList = taskMapper.selectAll();
        PageInfo<Task> pageInfo = new PageInfo<>(taskList);

        Map<String, Object> result = new HashMap<>();
        result.put("list", pageInfo.getList());
        result.put("total", pageInfo.getTotal());
        result.put("page", pageInfo.getPageNum());
        result.put("pageSize", pageInfo.getPageSize());

        return GraceJSONResult.ok(result);
    }

    @ApiOperation("查询我发布的任务")
    @GetMapping("/myPublished")
    public GraceJSONResult getMyPublishedTasks(@RequestParam String publisherId,
                                               @RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer pageSize) {
        Task condition = new Task();
        condition.setPublisherId(publisherId);
        PageHelper.startPage(page, pageSize);
        List<Task> taskList = taskMapper.select(condition);
        PageInfo<Task> pageInfo = new PageInfo<>(taskList);

        Map<String, Object> result = new HashMap<>();
        result.put("list", pageInfo.getList());
        result.put("total", pageInfo.getTotal());
        result.put("page", pageInfo.getPageNum());
        result.put("pageSize", pageInfo.getPageSize());

        return GraceJSONResult.ok(result);
    }

    @ApiOperation("查询我接的任务")
    @GetMapping("/myAccepted")
    public GraceJSONResult getMyAcceptedTasks(@RequestParam String runnerId,
                                              @RequestParam(defaultValue = "1") Integer page,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
        Task condition = new Task();
        condition.setRunnerId(runnerId);
        PageHelper.startPage(page, pageSize);
        List<Task> taskList = taskMapper.select(condition);
        PageInfo<Task> pageInfo = new PageInfo<>(taskList);

        Map<String, Object> result = new HashMap<>();
        result.put("list", pageInfo.getList());
        result.put("total", pageInfo.getTotal());
        result.put("page", pageInfo.getPageNum());
        result.put("pageSize", pageInfo.getPageSize());

        return GraceJSONResult.ok(result);
    }

    @ApiOperation("获取任务详情")
    @GetMapping("/detail")
    public GraceJSONResult getTaskDetail(@RequestParam String taskId) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        if (task == null) {
            GraceException.display(ResponseStatusEnum.TASK_NOT_FOUND);
        }
        return GraceJSONResult.ok(task);
    }

    @ApiOperation("评价跑腿员")
    @PostMapping("/evaluate")
    public GraceJSONResult evaluateRunner(@RequestParam String taskId,
                                          @RequestParam String publisherId,
                                          @RequestParam String publisherName,
                                          @RequestParam String runnerId,
                                          @RequestParam String runnerName,
                                          @RequestParam Integer rating,
                                          @RequestParam(required = false) String comment) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        if (task == null) {
            GraceException.display(ResponseStatusEnum.TASK_NOT_FOUND);
        }
        if (task.getStatus() != TaskStatus.COMPLETED.type) {
            GraceException.display(ResponseStatusEnum.TASK_STATUS_ERROR);
        }

        Evaluation evaluation = new Evaluation();
        evaluation.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        evaluation.setTaskId(taskId);
        evaluation.setPublisherId(publisherId);
        evaluation.setPublisherName(publisherName);
        evaluation.setRunnerId(runnerId);
        evaluation.setRunnerName(runnerName);
        evaluation.setRating(rating);
        evaluation.setComment(comment);
        evaluation.setCreatedTime(new Date());

        evaluationMapper.insert(evaluation);

        return GraceJSONResult.ok();
    }

    @ApiOperation("查询跑腿员评价")
    @GetMapping("/evaluations")
    public GraceJSONResult getRunnerEvaluations(@RequestParam String runnerId) {
        Evaluation condition = new Evaluation();
        condition.setRunnerId(runnerId);
        List<Evaluation> evaluations = evaluationMapper.select(condition);
        return GraceJSONResult.ok(evaluations);
    }

    /**
     * 发送系统消息
     */
    private void sendSystemMessage(String toUserId, String content) {
        try {
            if (StringUtils.isBlank(toUserId)) return;
            // 查找用户的会话（取第一个活跃会话）
            List<Conversation> convList = conversationService.getConversationList(toUserId);
            if (convList == null || convList.isEmpty()) {
                // 没有会话，创建临时会话（关联到最近的已完成任务）
                // 这里简化：不创建新会话，只记录日志
                System.out.println("用户 " + toUserId + " 暂无会话，消息未发送: " + content);
                return;
            }
            Conversation conv = convList.get(0);
            messageService.sendMessage(
                    conv.getId(),
                    "system", "系统", "",
                    toUserId,
                    content
            );
            System.out.println("系统消息已发送到: " + toUserId);
        } catch (Exception e) {
            System.err.println("发送系统消息失败: " + e.getMessage());
        }
    }
    @ApiOperation("获取跑腿员接单状态")
    @GetMapping("/getAcceptStatus")
    public GraceJSONResult getAcceptStatus(@RequestParam String runnerId) {
        String key = "runner_accept_status:" + runnerId;
        String status = redis.get(key);
        if (StringUtils.isBlank(status)) {
            // 默认开启
            return GraceJSONResult.ok(1);
        }
        return GraceJSONResult.ok(Integer.parseInt(status));
    }

    @ApiOperation("设置跑腿员接单状态")
    @PostMapping("/setAcceptStatus")
    public GraceJSONResult setAcceptStatus(@RequestParam String runnerId,
                                           @RequestParam Integer accepting) {
        String key = "runner_accept_status:" + runnerId;
        redis.set(key, String.valueOf(accepting), 86400); // 24小时过期
        return GraceJSONResult.ok();
    }
}