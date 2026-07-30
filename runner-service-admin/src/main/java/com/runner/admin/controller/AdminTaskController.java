package com.runner.admin.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.runner.enums.TaskStatus;
import com.runner.grace.result.GraceJSONResult;
import com.runner.pojo.Task;
import com.runner.admin.mapper.TaskMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "管理后台-任务管理")
@RestController
@RequestMapping("admin/task")
public class AdminTaskController {

    @Autowired
    private TaskMapper taskMapper;

    @ApiOperation("获取任务列表")
    @GetMapping("/list")
    public GraceJSONResult getTaskList(@RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       @RequestParam(required = false) Integer status,
                                       @RequestParam(required = false) String keyword) {
        PageHelper.startPage(page, pageSize);
        List<Task> list = taskMapper.selectAll();
        PageInfo<Task> pageInfo = new PageInfo<>(list);

        Map<String, Object> result = new HashMap<>();
        result.put("list", pageInfo.getList());
        result.put("total", pageInfo.getTotal());
        result.put("page", pageInfo.getPageNum());
        result.put("pageSize", pageInfo.getPageSize());

        return GraceJSONResult.ok(result);
    }

    @ApiOperation("强制取消任务")
    @PostMapping("/cancel")
    public GraceJSONResult cancelTask(@RequestParam String taskId) {
        // 实际应实现取消逻辑
        return GraceJSONResult.ok();
    }

    @ApiOperation("删除任务")
    @DeleteMapping("/delete")
    public GraceJSONResult deleteTask(@RequestParam String taskId) {
        taskMapper.deleteByPrimaryKey(taskId);
        return GraceJSONResult.ok();
    }
}