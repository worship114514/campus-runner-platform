package com.runner.user.controller;

import com.runner.enums.UserRole;
import com.runner.enums.UserStatus;
import com.runner.exception.GraceException;
import com.runner.grace.result.GraceJSONResult;
import com.runner.grace.result.ResponseStatusEnum;
import com.runner.pojo.AppUser;
import com.runner.pojo.RunnerApplication;
import com.runner.pojo.bo.UpdateUserInfoBO;
import com.runner.user.mapper.AppUserMapper;
import com.runner.user.mapper.RunnerApplicationMapper;
import com.runner.utils.JsonUtils;
import com.runner.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "用户信息")
@RestController
@RequestMapping("user")
public class UserController {

    public static final String REDIS_USER_INFO = "redis_user_info";

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private RunnerApplicationMapper runnerApplicationMapper;

    @Autowired
    private RedisOperator redis;

    @ApiOperation("获取用户信息")
    @PostMapping("/getUserInfo")
    public GraceJSONResult getUserInfo(@RequestParam String userId) {
        if (StringUtils.isBlank(userId)) {
            GraceException.display(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        AppUser user = appUserMapper.selectByPrimaryKey(userId);
        if (user == null) {
            GraceException.display(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        // 查询跑腿员申请状态
        RunnerApplication condition = new RunnerApplication();
        condition.setUserId(userId);
        List<RunnerApplication> apps = runnerApplicationMapper.select(condition);

        // 构建返回结果
        // 方法1：直接返回用户对象，额外加 runnerStatus 字段
        // 使用 Map 包装
        Map<String, Object> result = new HashMap<>();
        // 把用户信息复制到 Map（需要手动复制所有字段）
        result.put("id", user.getId());
        result.put("mobile", user.getMobile());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        result.put("face", user.getFace());
        result.put("realname", user.getRealname());
        result.put("email", user.getEmail());
        result.put("sex", user.getSex());
        result.put("birthday", user.getBirthday());
        result.put("province", user.getProvince());
        result.put("city", user.getCity());
        result.put("district", user.getDistrict());
        result.put("userRole", user.getUserRole());
        result.put("activeStatus", user.getActiveStatus());
        result.put("balance", user.getBalance());
        result.put("createdTime", user.getCreatedTime());
        result.put("updatedTime", user.getUpdatedTime());

        if (apps != null && !apps.isEmpty()) {
            result.put("runnerStatus", apps.get(0).getStatus());
        } else {
            result.put("runnerStatus", null);
        }

        user.setPassword(null);
        return GraceJSONResult.ok(result);
    }

    @ApiOperation("获取账户信息")
    @PostMapping("/getAccountInfo")
    public GraceJSONResult getAccountInfo(@RequestParam String userId) {
        AppUser user = appUserMapper.selectByPrimaryKey(userId);
        if (user == null) {
            GraceException.display(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        user.setPassword(null);
        return GraceJSONResult.ok(user);
    }

    @ApiOperation("更新用户信息（部分更新）")
    @PostMapping("/updateUserInfo")
    public GraceJSONResult updateUserInfo(@RequestBody @Valid UpdateUserInfoBO updateUserInfoBO) {
        AppUser user = new AppUser();
        user.setId(updateUserInfoBO.getId());
        user.setUpdatedTime(new Date());

        // 只更新传了的字段，没传的不更新
        if (StringUtils.isNotBlank(updateUserInfoBO.getNickname())) {
            user.setNickname(updateUserInfoBO.getNickname());
        }
        if (StringUtils.isNotBlank(updateUserInfoBO.getFace())) {
            user.setFace(updateUserInfoBO.getFace());
        }
        if (StringUtils.isNotBlank(updateUserInfoBO.getRealname())) {
            user.setRealname(updateUserInfoBO.getRealname());
        }
        if (StringUtils.isNotBlank(updateUserInfoBO.getEmail())) {
            user.setEmail(updateUserInfoBO.getEmail());
        }
        if (updateUserInfoBO.getSex() != null) {
            user.setSex(updateUserInfoBO.getSex());
        }
        if (updateUserInfoBO.getBirthday() != null) {
            user.setBirthday(updateUserInfoBO.getBirthday());
        }
        if (StringUtils.isNotBlank(updateUserInfoBO.getProvince())) {
            user.setProvince(updateUserInfoBO.getProvince());
        }
        if (StringUtils.isNotBlank(updateUserInfoBO.getCity())) {
            user.setCity(updateUserInfoBO.getCity());
        }
        if (StringUtils.isNotBlank(updateUserInfoBO.getDistrict())) {
            user.setDistrict(updateUserInfoBO.getDistrict());
        }

        appUserMapper.updateByPrimaryKeySelective(user);

        // 更新 Redis 缓存
        AppUser updatedUser = appUserMapper.selectByPrimaryKey(user.getId());
        redis.set(REDIS_USER_INFO + ":" + updatedUser.getId(),
                JsonUtils.objectToJson(updatedUser), 86400);

        updatedUser.setPassword(null);
        return GraceJSONResult.ok(updatedUser);
    }

    @ApiOperation("切换为跑腿员（已审核通过的用户）")
    @PostMapping("/switchToRunner")
    public GraceJSONResult switchToRunner(@RequestParam String userId) {
        // 1. 验证用户是否存在
        AppUser user = appUserMapper.selectByPrimaryKey(userId);
        if (user == null) {
            GraceException.display(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        // 2. 检查是否已通过跑腿员审核
        RunnerApplication condition = new RunnerApplication();
        condition.setUserId(userId);
        condition.setStatus(UserStatus.ACTIVE.type);
        List<RunnerApplication> apps = runnerApplicationMapper.select(condition);

        if (apps == null || apps.isEmpty()) {
            return GraceJSONResult.errorMsg("您还不是跑腿员，请先申请并通过审核");
        }

        // 3. 更新用户角色
        AppUser updateUser = new AppUser();
        updateUser.setId(userId);
        updateUser.setUserRole(UserRole.RUNNER.type);
        updateUser.setUpdatedTime(new Date());
        appUserMapper.updateByPrimaryKeySelective(updateUser);

        // 4. 更新缓存
        AppUser updatedUser = appUserMapper.selectByPrimaryKey(userId);
        redis.set(REDIS_USER_INFO + ":" + userId, JsonUtils.objectToJson(updatedUser), 86400);

        updatedUser.setPassword(null);
        return GraceJSONResult.ok(updatedUser);
    }

    @ApiOperation("切换为普通用户")
    @PostMapping("/switchToUser")
    public GraceJSONResult switchToUser(@RequestParam String userId) {
        // 1. 验证用户是否存在
        AppUser user = appUserMapper.selectByPrimaryKey(userId);
        if (user == null) {
            GraceException.display(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        // 2. 更新用户角色
        AppUser updateUser = new AppUser();
        updateUser.setId(userId);
        updateUser.setUserRole(UserRole.USER.type);
        updateUser.setUpdatedTime(new Date());
        appUserMapper.updateByPrimaryKeySelective(updateUser);

        // 3. 更新缓存
        AppUser updatedUser = appUserMapper.selectByPrimaryKey(userId);
        redis.set(REDIS_USER_INFO + ":" + userId, JsonUtils.objectToJson(updatedUser), 86400);

        updatedUser.setPassword(null);
        return GraceJSONResult.ok(updatedUser);
    }

    @ApiOperation("获取用户跑腿员状态")
    @GetMapping("/getRunnerStatus")
    public GraceJSONResult getRunnerStatus(@RequestParam String userId) {
        RunnerApplication condition = new RunnerApplication();
        condition.setUserId(userId);
        List<RunnerApplication> apps = runnerApplicationMapper.select(condition);

        if (apps == null || apps.isEmpty()) {
            return GraceJSONResult.ok(null);
        }

        RunnerApplication app = apps.get(0);
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("status", app.getStatus());
        result.put("id", app.getId());
        if (StringUtils.isNotBlank(app.getRemark())) {
            result.put("remark", app.getRemark());
        }
        return GraceJSONResult.ok(result);
    }
}