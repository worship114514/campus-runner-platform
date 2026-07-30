package com.runner.task.mapper;

import com.runner.my.mapper.MyMapper;
import com.runner.pojo.Order;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderMapper extends MyMapper<Order> {
}