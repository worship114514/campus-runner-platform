package com.runner.admin.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.runner.enums.UserStatus;
import com.runner.grace.result.GraceJSONResult;
import com.runner.pojo.AppUser;
import com.runner.admin.mapper.AppUserMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "管理后台-用户管理")
@RestController
@RequestMapping("admin/user")
public class AdminUserController {

    @Autowired
    private AppUserMapper appUserMapper;

    @ApiOperation("获取用户列表")
    @GetMapping("/list")
    public GraceJSONResult getUserList(@RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       @RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) Integer userRole) {
        PageHelper.startPage(page, pageSize);
        List<AppUser> list = appUserMapper.selectAll();
        PageInfo<AppUser> pageInfo = new PageInfo<>(list);

        Map<String, Object> result = new HashMap<>();
        result.put("list", pageInfo.getList());
        result.put("total", pageInfo.getTotal());
        result.put("page", pageInfo.getPageNum());
        result.put("pageSize", pageInfo.getPageSize());

        return GraceJSONResult.ok(result);
    }

    @ApiOperation("冻结/解冻用户")
    @PostMapping("/freeze")
    public GraceJSONResult freezeUser(@RequestParam String userId,
                                      @RequestParam Integer status) {
        AppUser user = new AppUser();
        user.setId(userId);
        user.setActiveStatus(status);
        user.setUpdatedTime(new Date());
        appUserMapper.updateByPrimaryKeySelective(user);
        return GraceJSONResult.ok();
    }
}