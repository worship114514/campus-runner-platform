package com.runner.user.controller;

import com.runner.enums.UserRole;
import com.runner.enums.UserStatus;
import com.runner.exception.GraceException;
import com.runner.grace.result.GraceJSONResult;
import com.runner.grace.result.ResponseStatusEnum;
import com.runner.pojo.AppUser;
import com.runner.pojo.RunnerApplication;
import com.runner.user.mapper.AppUserMapper;
import com.runner.user.mapper.RunnerApplicationMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Api(tags = "跑腿员相关")
@RestController
@RequestMapping("runner")
public class RunnerController {

    @Autowired
    private RunnerApplicationMapper runnerApplicationMapper;

    @Autowired
    private AppUserMapper appUserMapper;

    @ApiOperation("申请成为跑腿员")
    @PostMapping("/apply")
    public GraceJSONResult applyRunner(@RequestParam String userId,
                                       @RequestParam String realName,
                                       @RequestParam String idCard,
                                       @RequestParam String phone,
                                       @RequestParam String faceImage) {
        AppUser user = appUserMapper.selectByPrimaryKey(userId);
        if (user == null) {
            GraceException.display(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        RunnerApplication existing = new RunnerApplication();
        existing.setUserId(userId);
        List<RunnerApplication> list = runnerApplicationMapper.select(existing);
        if (list != null && !list.isEmpty()) {
            return GraceJSONResult.errorMsg("您已提交过申请，请勿重复提交");
        }

        RunnerApplication application = new RunnerApplication();
        application.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        application.setUserId(userId);
        application.setRealName(realName);
        application.setIdCard(idCard);
        application.setPhone(phone);
        application.setFaceImage(faceImage);
        application.setStatus(UserStatus.PENDING.type);
        application.setCreatedTime(new Date());
        application.setUpdatedTime(new Date());

        runnerApplicationMapper.insert(application);

        return GraceJSONResult.ok("申请已提交，等待管理员审核");
    }

    @ApiOperation("查询跑腿员申请状态")
    @GetMapping("/getStatus")
    public GraceJSONResult getRunnerStatus(@RequestParam String userId) {
        RunnerApplication condition = new RunnerApplication();
        condition.setUserId(userId);
        List<RunnerApplication> list = runnerApplicationMapper.select(condition);

        if (list == null || list.isEmpty()) {
            return GraceJSONResult.ok(null);
        }

        RunnerApplication application = list.get(0);
        return GraceJSONResult.ok(application);
    }

    @ApiOperation("管理员审核跑腿员申请")
    @PostMapping("/audit")
    public GraceJSONResult auditRunner(@RequestParam String applicationId,
                                       @RequestParam Integer status,
                                       @RequestParam(required = false) String remark) {
        RunnerApplication application = runnerApplicationMapper.selectByPrimaryKey(applicationId);
        if (application == null) {
            GraceException.display(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        application.setStatus(status);
        application.setRemark(remark);
        application.setUpdatedTime(new Date());
        runnerApplicationMapper.updateByPrimaryKeySelective(application);

        if (status == UserStatus.ACTIVE.type) {
            AppUser user = new AppUser();
            user.setId(application.getUserId());
            user.setUserRole(UserRole.RUNNER.type);
            user.setActiveStatus(UserStatus.ACTIVE.type);
            appUserMapper.updateByPrimaryKeySelective(user);
        }

        return GraceJSONResult.ok();
    }

    @ApiOperation("获取待审核跑腿员列表")
    @GetMapping("/pendingList")
    public GraceJSONResult getPendingRunners() {
        RunnerApplication condition = new RunnerApplication();
        condition.setStatus(UserStatus.PENDING.type);
        List<RunnerApplication> list = runnerApplicationMapper.select(condition);
        return GraceJSONResult.ok(list);
    }
}