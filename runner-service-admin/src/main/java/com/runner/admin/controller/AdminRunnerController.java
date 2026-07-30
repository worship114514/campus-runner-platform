package com.runner.admin.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.runner.enums.UserRole;
import com.runner.enums.UserStatus;
import com.runner.grace.result.GraceJSONResult;
import com.runner.pojo.AppUser;
import com.runner.pojo.RunnerApplication;
import com.runner.admin.mapper.RunnerApplicationMapper;
import com.runner.admin.mapper.AppUserMapper;
import com.runner.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "管理后台-跑腿员管理")
@RestController
@RequestMapping("admin/runner")
public class AdminRunnerController {

    @Autowired
    private RunnerApplicationMapper runnerApplicationMapper;

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private RedisOperator redis;

    // ========== 审核列表（待审核的申请） ==========
    @ApiOperation("获取待审核跑腿员列表")
    @GetMapping("/pending")
    public GraceJSONResult getPendingRunners(@RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        RunnerApplication condition = new RunnerApplication();
        condition.setStatus(UserStatus.PENDING.type);
        List<RunnerApplication> list = runnerApplicationMapper.select(condition);

        // ===== 新增：为每个申请生成人脸图片URL =====
        for (RunnerApplication app : list) {
            if (app.getFaceId() != null && !app.getFaceId().isEmpty()) {
                String faceUrl = "http://files.runner.gzmu.com:8004/fs/readFaceImage?faceId=" + app.getFaceId();
                app.setFaceImage(faceUrl);
            }
        }
        // ===== 新增结束 =====

        PageInfo<RunnerApplication> pageInfo = new PageInfo<>(list);

        Map<String, Object> result = new HashMap<>();
        result.put("list", pageInfo.getList());
        result.put("total", pageInfo.getTotal());
        result.put("page", pageInfo.getPageNum());
        result.put("pageSize", pageInfo.getPageSize());

        return GraceJSONResult.ok(result);
    }

    // ========== 所有跑腿员列表（包含已通过、已冻结） ==========
    @ApiOperation("获取所有跑腿员列表")
    @GetMapping("/list")
    public GraceJSONResult getRunnerList(@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                         @RequestParam(required = false) Integer status) {
        PageHelper.startPage(page, pageSize);

        // 查询所有 role = 2 的用户（跑腿员）
        AppUser condition = new AppUser();
        condition.setUserRole(UserRole.RUNNER.type);
        if (status != null) {
            condition.setActiveStatus(status);
        }
        List<AppUser> userList = appUserMapper.select(condition);

        // ===== 新增：为每个用户查询人脸ID =====
        for (AppUser user : userList) {
            // 根据 userId 查询跑腿员申请表，获取 faceId
            RunnerApplication appCondition = new RunnerApplication();
            appCondition.setUserId(user.getId());
            // 按创建时间倒序，取最新的申请记录
            List<RunnerApplication> appList = runnerApplicationMapper.select(appCondition);
            if (appList != null && !appList.isEmpty()) {
                String faceId = appList.get(0).getFaceId();
                if (faceId != null && !faceId.isEmpty()) {
                    // 将人脸图片URL放入 face 字段（复用，前端直接用）
                    String faceUrl = "http://files.runner.gzmu.com:8004/fs/readFaceImage?faceId=" + faceId;
                    user.setFace(faceUrl);
                }
            }
        }
        // ===== 新增结束 =====

        PageInfo<AppUser> pageInfo = new PageInfo<>(userList);

        Map<String, Object> result = new HashMap<>();
        result.put("list", pageInfo.getList());
        result.put("total", pageInfo.getTotal());
        result.put("page", pageInfo.getPageNum());
        result.put("pageSize", pageInfo.getPageSize());

        return GraceJSONResult.ok(result);
    }

    // ========== 审核通过 ==========
    @ApiOperation("审核通过")
    @PostMapping("/approve")
    public GraceJSONResult approveRunner(@RequestParam String applicationId) {
        RunnerApplication application = runnerApplicationMapper.selectByPrimaryKey(applicationId);
        if (application == null) {
            return GraceJSONResult.errorMsg("申请记录不存在");
        }

        if (application.getStatus() != UserStatus.PENDING.type) {
            return GraceJSONResult.errorMsg("该申请已处理");
        }

        // 更新申请状态
        application.setStatus(UserStatus.ACTIVE.type);
        application.setUpdatedTime(new Date());
        runnerApplicationMapper.updateByPrimaryKeySelective(application);

        // 更新用户角色为跑腿员
        AppUser user = new AppUser();
        user.setId(application.getUserId());
        user.setUserRole(UserRole.RUNNER.type);
        user.setActiveStatus(UserStatus.ACTIVE.type);
        user.setUpdatedTime(new Date());
        appUserMapper.updateByPrimaryKeySelective(user);

        redis.del("redis_user_info:" + application.getUserId());

        return GraceJSONResult.ok("审核通过，用户已成为跑腿员");
    }

    // ========== 审核拒绝 ==========
    @ApiOperation("审核拒绝")
    @PostMapping("/reject")
    public GraceJSONResult rejectRunner(@RequestParam String applicationId,
                                        @RequestParam(required = false) String reason) {
        RunnerApplication application = runnerApplicationMapper.selectByPrimaryKey(applicationId);
        if (application == null) {
            return GraceJSONResult.errorMsg("申请记录不存在");
        }

        if (application.getStatus() != UserStatus.PENDING.type) {
            return GraceJSONResult.errorMsg("该申请已处理");
        }

        application.setStatus(UserStatus.FROZEN.type);
        application.setRemark(reason);
        application.setUpdatedTime(new Date());
        runnerApplicationMapper.updateByPrimaryKeySelective(application);

        return GraceJSONResult.ok("已拒绝");
    }

    // ========== 冻结跑腿员 ==========
    @ApiOperation("冻结跑腿员")
    @PostMapping("/freeze")
    public GraceJSONResult freezeRunner(@RequestParam String userId) {
        AppUser user = appUserMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return GraceJSONResult.errorMsg("用户不存在");
        }

        if (user.getUserRole() != UserRole.RUNNER.type) {
            return GraceJSONResult.errorMsg("该用户不是跑腿员");
        }

        user.setActiveStatus(UserStatus.FROZEN.type);
        user.setUpdatedTime(new Date());
        appUserMapper.updateByPrimaryKeySelective(user);

        redis.del("redis_user_info:" + userId);

        return GraceJSONResult.ok("已冻结");
    }

    // ========== 解冻跑腿员 ==========
    @ApiOperation("解冻跑腿员")
    @PostMapping("/unfreeze")
    public GraceJSONResult unfreezeRunner(@RequestParam String userId) {
        AppUser user = appUserMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return GraceJSONResult.errorMsg("用户不存在");
        }

        if (user.getUserRole() != UserRole.RUNNER.type) {
            return GraceJSONResult.errorMsg("该用户不是跑腿员");
        }

        user.setActiveStatus(UserStatus.ACTIVE.type);
        user.setUpdatedTime(new Date());
        appUserMapper.updateByPrimaryKeySelective(user);

        redis.del("redis_user_info:" + userId);

        return GraceJSONResult.ok("已解冻");
    }

    // ========== 删除跑腿员 ==========
    @ApiOperation("删除跑腿员（慎用）")
    @DeleteMapping("/delete")
    public GraceJSONResult deleteRunner(@RequestParam String userId) {
        AppUser user = appUserMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return GraceJSONResult.errorMsg("用户不存在");
        }

        // 将用户角色改为普通用户
        user.setUserRole(UserRole.USER.type);
        user.setUpdatedTime(new Date());
        appUserMapper.updateByPrimaryKeySelective(user);

        redis.del("redis_user_info:" + userId);

        return GraceJSONResult.ok("已删除跑腿员身份");
    }
    @ApiOperation("获取跑腿员详情")
    @GetMapping("/detail")
    public GraceJSONResult getRunnerDetail(@RequestParam String userId) {
        AppUser user = appUserMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return GraceJSONResult.errorMsg("用户不存在");
        }
        return GraceJSONResult.ok(user);
    }
}