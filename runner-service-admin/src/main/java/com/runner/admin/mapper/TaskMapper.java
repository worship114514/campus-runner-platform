package com.runner.admin.mapper;

import com.runner.my.mapper.MyMapper;
import com.runner.pojo.Task;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskMapper extends MyMapper<Task> {
}