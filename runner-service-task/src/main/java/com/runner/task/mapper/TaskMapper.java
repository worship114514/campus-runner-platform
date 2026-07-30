package com.runner.task.mapper;

import com.runner.my.mapper.MyMapper;
import com.runner.pojo.Task;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskMapper extends MyMapper<Task> {
}