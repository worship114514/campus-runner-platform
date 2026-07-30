package com.runner.admin.mapper;

import com.runner.my.mapper.MyMapper;
import com.runner.pojo.AdminUser;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminUserMapper extends MyMapper<AdminUser> {
}