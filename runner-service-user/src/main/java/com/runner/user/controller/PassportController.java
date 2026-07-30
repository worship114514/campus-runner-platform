package com.runner.user.controller;

import com.runner.enums.UserRole;
import com.runner.enums.UserStatus;
import com.runner.exception.GraceException;
import com.runner.grace.result.GraceJSONResult;
import com.runner.grace.result.ResponseStatusEnum;
import com.runner.pojo.AppUser;
import com.runner.pojo.RunnerApplication;
import com.runner.pojo.bo.RegisterLoginBO;
import com.runner.user.mapper.AppUserMapper;
import com.runner.user.mapper.RunnerApplicationMapper;
import com.runner.utils.IPUtil;
import com.runner.utils.RedisOperator;
import com.runner.utils.SMSUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

@Api(tags = "用户注册登录")
@RestController
@RequestMapping("passport")
public class PassportController {

    private static final String MOBILE_SMSCODE = "mobile:smscode";
    // 修改手机号验证码前缀
    private static final String CHANGE_MOBILE_SMSCODE = "change_mobile:smscode";

    @Autowired
    private SMSUtils smsUtils;

    @Autowired
    private RedisOperator redis;

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private RunnerApplicationMapper runnerApplicationMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ========== 注册（统一为普通用户） ==========
    @ApiOperation("注册")
    @PostMapping("/register")
    public GraceJSONResult register(@Validated(RegisterLoginBO.RegisterGroup.class) @RequestBody RegisterLoginBO registerLoginBO) {
        String mobile = registerLoginBO.getMobile();
        String password = registerLoginBO.getPassword();
        String nickname = registerLoginBO.getNickname();

        if (StringUtils.isBlank(password) || password.length() < 6) {
            GraceException.display(ResponseStatusEnum.FAILED);
        }

        AppUser condition = new AppUser();
        condition.setMobile(mobile);
        AppUser existing = appUserMapper.selectOne(condition);
        if (existing != null) {
            GraceException.display(ResponseStatusEnum.MOBILE_ALREADY_EXISTS);
        }

        String userId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        AppUser user = new AppUser();
        user.setId(userId);
        user.setMobile(mobile);
        user.setUsername("user_" + mobile.substring(7));
        user.setNickname(nickname);
        user.setPassword(passwordEncoder.encode(password));
        user.setUserRole(UserRole.USER.type);
        user.setActiveStatus(UserStatus.ACTIVE.type);
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());
        user.setBalance(BigDecimal.ZERO);

        appUserMapper.insert(user);

        user.setPassword(null);
        return GraceJSONResult.ok(user);
    }

    // ========== 获取短信验证码 ==========
    @ApiOperation("获取短信验证码")
    @GetMapping("/getSMSCode")
    public GraceJSONResult getSMSCode(@RequestParam String mobile) throws Exception {
        if (StringUtils.isBlank(mobile)) {
            return GraceJSONResult.errorMsg("手机号不能为空");
        }

        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        System.out.println("========== 验证码 ==========");
        System.out.println("手机号: " + mobile);
        System.out.println("验证码: " + code);
        System.out.println("============================");

        redis.set("mobile:smscode:" + mobile, code, 300);
        return GraceJSONResult.ok();
    }

    // ========== 手机号+验证码登录 ==========
    @ApiOperation("手机号+验证码登录")
    @PostMapping("/doLogin")
    public GraceJSONResult doLogin(@Validated(RegisterLoginBO.LoginGroup.class) @RequestBody RegisterLoginBO registerLoginBO) {
        String mobile = registerLoginBO.getMobile();
        String smsCode = registerLoginBO.getSmsCode();

        String redisCode = redis.get(MOBILE_SMSCODE + ":" + mobile);
        if (StringUtils.isBlank(redisCode) || !redisCode.equals(smsCode)) {
            GraceException.display(ResponseStatusEnum.SMS_CODE_ERROR);
        }

        AppUser condition = new AppUser();
        condition.setMobile(mobile);
        AppUser user = appUserMapper.selectOne(condition);

        if (user == null) {
            GraceException.display(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        if (user.getActiveStatus() == UserStatus.FROZEN.type) {
            GraceException.display(ResponseStatusEnum.USER_FROZEN);
        }

        // 如果是跑腿员且状态为审核中，不允许登录
        if (user.getUserRole() == UserRole.RUNNER.type && user.getActiveStatus() == UserStatus.PENDING.type) {
            GraceException.display(ResponseStatusEnum.RUNNER_NOT_APPROVED);
        }

        String token = UUID.randomUUID().toString();
        redis.set("redis_user_token:" + user.getId(), token, 86400);

        // 查询跑腿员申请状态
        RunnerApplication appCondition = new RunnerApplication();
        appCondition.setUserId(user.getId());
        List<RunnerApplication> apps = runnerApplicationMapper.select(appCondition);

        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("token", token);
        if (apps != null && !apps.isEmpty()) {
            result.put("runnerStatus", apps.get(0).getStatus());
        }

        user.setPassword(null);
        return GraceJSONResult.ok(result);
    }

    // ========== 密码登录 ==========
    @ApiOperation("密码登录")
    @PostMapping("/doLoginByPwd")
    public GraceJSONResult doLoginByPwd(@RequestBody RegisterLoginBO registerLoginBO) {
        String mobile = registerLoginBO.getMobile();
        String password = registerLoginBO.getPassword();

        if (StringUtils.isBlank(mobile) || StringUtils.isBlank(password)) {
            GraceException.display(ResponseStatusEnum.FAILED);
        }

        AppUser condition = new AppUser();
        condition.setMobile(mobile);
        AppUser user = appUserMapper.selectOne(condition);

        if (user == null) {
            GraceException.display(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            GraceException.display(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        if (user.getActiveStatus() == UserStatus.FROZEN.type) {
            GraceException.display(ResponseStatusEnum.USER_FROZEN);
        }

        if (user.getUserRole() == UserRole.RUNNER.type && user.getActiveStatus() == UserStatus.PENDING.type) {
            GraceException.display(ResponseStatusEnum.RUNNER_NOT_APPROVED);
        }

        String token = UUID.randomUUID().toString();
        redis.set("redis_user_token:" + user.getId(), token, 86400);

        // 查询跑腿员申请状态
        RunnerApplication appCondition = new RunnerApplication();
        appCondition.setUserId(user.getId());
        List<RunnerApplication> apps = runnerApplicationMapper.select(appCondition);

        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("token", token);
        if (apps != null && !apps.isEmpty()) {
            result.put("runnerStatus", apps.get(0).getStatus());
        }

        user.setPassword(null);
        return GraceJSONResult.ok(result);
    }

    // ========== 退出登录 ==========
    @ApiOperation("退出登录")
    @PostMapping("/logout")
    public GraceJSONResult logout(@RequestParam String userId) {
        redis.del("redis_user_token:" + userId);
        return GraceJSONResult.ok();
    }

    // ========== 申请成为跑腿员 ==========
    @ApiOperation("申请成为跑腿员")
    @PostMapping("/applyRunner")
    public GraceJSONResult applyRunner(@RequestParam String userId,
                                       @RequestParam String realName,
                                       @RequestParam String idCard,
                                       @RequestParam String phone,
                                       @RequestParam(required = false) String faceId) {
        AppUser user = appUserMapper.selectByPrimaryKey(userId);
        if (user == null) {
            GraceException.display(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        if (user.getUserRole() == UserRole.RUNNER.type) {
            return GraceJSONResult.errorMsg("您已经是跑腿员了");
        }

        RunnerApplication condition = new RunnerApplication();
        condition.setUserId(userId);
        List<RunnerApplication> existing = runnerApplicationMapper.select(condition);

        if (existing != null && !existing.isEmpty()) {
            RunnerApplication app = existing.get(0);
            if (app.getStatus() == UserStatus.PENDING.type) {
                return GraceJSONResult.errorMsg("您的申请正在审核中，请耐心等待");
            }
            if (app.getStatus() == UserStatus.ACTIVE.type) {
                return GraceJSONResult.errorMsg("您已经是跑腿员了");
            }
            if (app.getStatus() == UserStatus.FROZEN.type) {
                app.setRealName(realName);
                app.setIdCard(idCard);
                app.setPhone(phone);
                if (faceId != null && !faceId.isEmpty()) {
                    app.setFaceId(faceId);
                }
                app.setStatus(UserStatus.PENDING.type);
                app.setRemark(null);
                app.setUpdatedTime(new Date());
                runnerApplicationMapper.updateByPrimaryKeySelective(app);
                return GraceJSONResult.ok("申请已重新提交，等待管理员审核");
            }
        }

        RunnerApplication application = new RunnerApplication();
        application.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        application.setUserId(userId);
        application.setRealName(realName);
        application.setIdCard(idCard);
        application.setPhone(phone);
        if (faceId != null && !faceId.isEmpty()) {
            application.setFaceId(faceId);
        }
        application.setStatus(UserStatus.PENDING.type);
        application.setCreatedTime(new Date());
        application.setUpdatedTime(new Date());

        runnerApplicationMapper.insert(application);

        return GraceJSONResult.ok("申请已提交，等待管理员审核");
    }

    // ========== 查询跑腿员申请状态 ==========
    @ApiOperation("查询跑腿员申请状态")
    @GetMapping("/getRunnerStatus")
    public GraceJSONResult getRunnerStatus(@RequestParam String userId) {
        RunnerApplication condition = new RunnerApplication();
        condition.setUserId(userId);
        List<RunnerApplication> list = runnerApplicationMapper.select(condition);

        if (list == null || list.isEmpty()) {
            return GraceJSONResult.ok(null);
        }

        RunnerApplication app = list.get(0);
        Map<String, Object> result = new HashMap<>();
        result.put("status", app.getStatus());
        result.put("id", app.getId());
        result.put("realName", app.getRealName());
        result.put("phone", app.getPhone());
        if (StringUtils.isNotBlank(app.getRemark())) {
            result.put("remark", app.getRemark());
        }
        return GraceJSONResult.ok(result);
    }
    @ApiOperation("发送修改手机号验证码")
    @GetMapping("/sendChangeMobileCode")
    public GraceJSONResult sendChangeMobileCode(@RequestParam String userId,
                                                @RequestParam String newMobile,
                                                HttpServletRequest request) {
        // 1. 校验用户是否存在
        AppUser user = appUserMapper.selectByPrimaryKey(userId);
        if (user == null) {
            GraceException.display(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        // 2. 校验新手机号不能和当前手机号相同
        if (user.getMobile().equals(newMobile)) {
            return GraceJSONResult.errorMsg("新手机号与当前手机号相同");
        }

        // 3. 校验新手机号是否已被占用
        AppUser condition = new AppUser();
        condition.setMobile(newMobile);
        AppUser existing = appUserMapper.selectOne(condition);
        if (existing != null && !existing.getId().equals(userId)) {
            return GraceJSONResult.errorMsg("该手机号已被其他用户绑定");
        }

        // 4. 校验手机号格式
        if (!newMobile.matches("^1[3-9]\\d{9}$")) {
            return GraceJSONResult.errorMsg("请输入正确的手机号");
        }

        // 5. IP防刷
        String userIP = IPUtil.getRequestIp(request);
        String ipKey = "change_mobile:ip:" + userIP;
        if (redis.keyIsExist(ipKey)) {
            GraceException.display(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
        }
        redis.setnx60s(ipKey, userIP);

        // 6. 生成6位验证码
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        System.out.println("========== 修改手机号验证码 ==========");
        System.out.println("新手机号: " + newMobile);
        System.out.println("验证码: " + code);
        System.out.println("======================================");

        // 7. 存入Redis（5分钟过期）- 使用手机号作为key
        redis.set(CHANGE_MOBILE_SMSCODE + ":" + newMobile, code, 300);

        // 8. 调用阿里云短信发送
        try {
            smsUtils.sendSMS(newMobile, code);
        } catch (Exception e) {
            System.err.println("短信发送失败，但验证码已生成: " + code);
        }

        return GraceJSONResult.ok("验证码已发送");
    }

    @ApiOperation("确认修改手机号")
    @PostMapping("/confirmChangeMobile")
    public GraceJSONResult confirmChangeMobile(@RequestParam String userId,
                                               @RequestParam String newMobile,
                                               @RequestParam String smsCode,
                                               HttpServletResponse response) {
        // 1. 校验用户是否存在
        AppUser user = appUserMapper.selectByPrimaryKey(userId);
        if (user == null) {
            GraceException.display(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        // 2. 校验新手机号不能和当前手机号相同
        if (user.getMobile().equals(newMobile)) {
            return GraceJSONResult.errorMsg("新手机号与当前手机号相同");
        }

        // 3. 校验手机号格式
        if (!newMobile.matches("^1[3-9]\\d{9}$")) {
            return GraceJSONResult.errorMsg("请输入正确的手机号");
        }

        // 4. 校验验证码
        String redisCode = redis.get(CHANGE_MOBILE_SMSCODE + ":" + newMobile);
        System.out.println("Redis验证码: " + redisCode + ", 用户输入: " + smsCode);
        if (StringUtils.isBlank(redisCode) || !redisCode.equals(smsCode)) {
            GraceException.display(ResponseStatusEnum.SMS_CODE_ERROR);
        }

        // 5. 校验新手机号是否已被其他用户占用
        AppUser condition = new AppUser();
        condition.setMobile(newMobile);
        AppUser existing = appUserMapper.selectOne(condition);
        if (existing != null && !existing.getId().equals(userId)) {
            return GraceJSONResult.errorMsg("该手机号已被其他用户绑定");
        }

        // 6. 更新手机号
        AppUser updateUser = new AppUser();
        updateUser.setId(userId);
        updateUser.setMobile(newMobile);
        updateUser.setUpdatedTime(new Date());
        appUserMapper.updateByPrimaryKeySelective(updateUser);

        // 7. 删除验证码
        redis.del(CHANGE_MOBILE_SMSCODE + ":" + newMobile);

        // 8. 清除缓存的用户信息
        redis.del("redis_user_info:" + userId);

        return GraceJSONResult.ok("手机号修改成功");
    }

    @ApiOperation("修改密码")
    @PostMapping("/changePassword")
    public GraceJSONResult changePassword(@RequestParam String userId,
                                          @RequestParam String oldPassword,
                                          @RequestParam String newPassword) {
        // 1. 校验用户是否存在
        AppUser user = appUserMapper.selectByPrimaryKey(userId);
        if (user == null) {
            GraceException.display(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        // 2. 校验旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return GraceJSONResult.errorMsg("当前密码错误");
        }

        // 3. 校验新密码长度
        if (newPassword == null || newPassword.length() < 6) {
            return GraceJSONResult.errorMsg("新密码至少6位");
        }

        // 4. 更新密码
        AppUser updateUser = new AppUser();
        updateUser.setId(userId);
        updateUser.setPassword(passwordEncoder.encode(newPassword));
        updateUser.setUpdatedTime(new Date());
        appUserMapper.updateByPrimaryKeySelective(updateUser);

        // 5. 清除缓存的用户信息
        redis.del("redis_user_info:" + userId);

        // 6. 删除旧的Token，强制重新登录
        redis.del("redis_user_token:" + userId);

        return GraceJSONResult.ok("密码修改成功，请重新登录");
    }
}