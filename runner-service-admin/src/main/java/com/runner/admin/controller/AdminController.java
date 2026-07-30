package com.runner.admin.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.runner.exception.GraceException;
import com.runner.grace.result.GraceJSONResult;
import com.runner.grace.result.ResponseStatusEnum;
import com.runner.pojo.AdminUser;
import com.runner.admin.mapper.AdminUserMapper;
import com.runner.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Api(tags = "管理后台")
@RestController
@RequestMapping("admin")
public class AdminController {

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Autowired
    private RedisOperator redis;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @ApiOperation("管理员登录")
    @PostMapping("/login")
    public GraceJSONResult adminLogin(@RequestParam String username,
                                      @RequestParam String password) {
        // 1. 参数校验
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            GraceException.display(ResponseStatusEnum.FAILED);
        }

        // 2. 查询管理员
        AdminUser condition = new AdminUser();
        condition.setUsername(username);
        AdminUser adminUser = adminUserMapper.selectOne(condition);

        if (adminUser == null) {
            System.out.println("管理员不存在: " + username);
            GraceException.display(ResponseStatusEnum.ADMIN_NOT_EXIT_ERROR);
        }

        // 3. 打印调试信息
        System.out.println("========== 管理员登录调试 ==========");
        System.out.println("输入密码: " + password);
        System.out.println("数据库密码: " + adminUser.getPassword());
        System.out.println("密码匹配结果: " + passwordEncoder.matches(password, adminUser.getPassword()));
        System.out.println("=====================================");

        // 4. BCrypt 密码匹配
        if (!passwordEncoder.matches(password, adminUser.getPassword())) {
            GraceException.display(ResponseStatusEnum.ADMIN_NOT_EXIT_ERROR);
        }

        // 5. 生成Token
        String uniqueToken = UUID.randomUUID().toString();
        redis.set("redis_admin_token:" + adminUser.getId(), uniqueToken, 86400);

        // 返回 admin 和 token
        Map<String, Object> result = new HashMap<>();
        adminUser.setPassword(null);
        result.put("admin", adminUser);
        result.put("token", uniqueToken);

        return GraceJSONResult.ok(result);
    }

    @ApiOperation("管理员退出登录")
    @PostMapping("/logout")
    public GraceJSONResult adminLogout(@RequestParam String adminId) {
        redis.del("redis_admin_token:" + adminId);
        return GraceJSONResult.ok();
    }

    @ApiOperation("添加管理员")
    @PostMapping("/add")
    public GraceJSONResult addAdmin(@RequestParam String username,
                                    @RequestParam String password,
                                    @RequestParam String adminName) {
        AdminUser condition = new AdminUser();
        condition.setUsername(username);
        AdminUser existing = adminUserMapper.selectOne(condition);
        if (existing != null) {
            GraceException.display(ResponseStatusEnum.ADMIN_USERNAME_EXIST_ERROR);
        }

        AdminUser adminUser = new AdminUser();
        adminUser.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        adminUser.setUsername(username);
        adminUser.setPassword(passwordEncoder.encode(password));
        adminUser.setAdminName(adminName);
        adminUser.setCreatedTime(new Date());
        adminUser.setUpdatedTime(new Date());

        adminUserMapper.insert(adminUser);
        return GraceJSONResult.ok();
    }

    @ApiOperation("获取管理员列表")
    @GetMapping("/list")
    public GraceJSONResult getAdminList(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<AdminUser> list = adminUserMapper.selectAll();
        PageInfo<AdminUser> pageInfo = new PageInfo<>(list);

        Map<String, Object> result = new HashMap<>();
        result.put("list", pageInfo.getList());
        result.put("total", pageInfo.getTotal());
        result.put("page", pageInfo.getPageNum());
        result.put("pageSize", pageInfo.getPageSize());

        return GraceJSONResult.ok(result);
    }

    @ApiOperation("删除管理员")
    @DeleteMapping("/delete")
    public GraceJSONResult deleteAdmin(@RequestParam String adminId) {
        adminUserMapper.deleteByPrimaryKey(adminId);
        return GraceJSONResult.ok();
    }
    @ApiOperation("生成 BCrypt 密码（开发用）")
    @GetMapping("/encode")
    public GraceJSONResult encode(@RequestParam String password) {
        String encoded = passwordEncoder.encode(password);
        System.out.println("原始密码: " + password);
        System.out.println("加密后: " + encoded);
        return GraceJSONResult.ok(encoded);
    }
}