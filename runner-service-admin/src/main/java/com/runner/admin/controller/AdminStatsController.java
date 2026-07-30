package com.runner.admin.controller;

import com.runner.grace.result.GraceJSONResult;
import com.runner.pojo.Task;
import com.runner.pojo.AppUser;
import com.runner.admin.mapper.TaskMapper;
import com.runner.admin.mapper.AppUserMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Api(tags = "管理后台-数据统计")
@RestController
@RequestMapping("admin/stats")
public class AdminStatsController {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private AppUserMapper appUserMapper;

    @ApiOperation("获取统计数据")
    @GetMapping("/overview")
    public GraceJSONResult getStats() {
        // 总用户
        int totalUsers = appUserMapper.selectCount(new AppUser());

        // 跑腿员（role=2）
        AppUser runnerCondition = new AppUser();
        runnerCondition.setUserRole(2);
        int totalRunners = appUserMapper.selectCount(runnerCondition);

        // 总任务
        int totalTasks = taskMapper.selectCount(new Task());

        // 已完成任务
        Task completedCondition = new Task();
        completedCondition.setStatus(4);
        int completedTasks = taskMapper.selectCount(completedCondition);

        // 平台流水（所有已完成任务的酬劳总和）
        // 需要自定义查询，使用Mapper XML或直接用SQL
        BigDecimal totalAmount = getTotalTaskAmount();

        Map<String, Object> result = new HashMap<>();
        result.put("totalUsers", totalUsers);
        result.put("totalRunners", totalRunners);
        result.put("totalTasks", totalTasks);
        result.put("completedTasks", completedTasks);
        result.put("totalAmount", totalAmount);

        return GraceJSONResult.ok(result);
    }

    @ApiOperation("获取趋势数据（近7天）")
    @GetMapping("/trend")
    public GraceJSONResult getTrend() {
        List<String> dates = new ArrayList<>();
        List<Integer> taskCounts = new ArrayList<>();
        List<Integer> userCounts = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();

        // 获取近7天的日期
        for (int i = 6; i >= 0; i--) {
            Calendar day = Calendar.getInstance();
            day.add(Calendar.DAY_OF_MONTH, -i);
            String dateStr = sdf.format(day.getTime());
            dates.add(dateStr);

            // 查询当天创建的任务数
            Example taskExample = new Example(Task.class);
            Example.Criteria taskCriteria = taskExample.createCriteria();
            taskCriteria.andGreaterThanOrEqualTo("createdTime", getStartOfDay(day.getTime()));
            taskCriteria.andLessThan("createdTime", getEndOfDay(day.getTime()));
            int taskCount = taskMapper.selectCountByExample(taskExample);
            taskCounts.add(taskCount);

            // 查询当天注册的用户数
            Example userExample = new Example(AppUser.class);
            Example.Criteria userCriteria = userExample.createCriteria();
            userCriteria.andGreaterThanOrEqualTo("createdTime", getStartOfDay(day.getTime()));
            userCriteria.andLessThan("createdTime", getEndOfDay(day.getTime()));
            int userCount = appUserMapper.selectCountByExample(userExample);
            userCounts.add(userCount);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dates", dates);
        result.put("tasks", taskCounts);
        result.put("users", userCounts);

        return GraceJSONResult.ok(result);
    }

    // ===== 私有方法 =====

    private BigDecimal getTotalTaskAmount() {
        // 使用SQL聚合查询（通过Mapper XML实现）
        // 简单方式：查询所有已完成任务，累加酬劳
        Task condition = new Task();
        condition.setStatus(4);
        List<Task> completedTasks = taskMapper.select(condition);
        BigDecimal total = BigDecimal.ZERO;
        for (Task task : completedTasks) {
            if (task.getRewardAmount() != null) {
                total = total.add(task.getRewardAmount());
            }
        }
        return total;
    }

    private Date getStartOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date getEndOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
}