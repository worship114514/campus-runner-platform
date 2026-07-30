package com.runner.user.mapper;

import com.runner.my.mapper.MyMapper;
import com.runner.pojo.AppUser;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserMapper extends MyMapper<AppUser> {
}