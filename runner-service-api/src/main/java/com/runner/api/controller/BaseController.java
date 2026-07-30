package com.runner.api.controller;

import com.runner.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseController {

    @Autowired
    public RedisOperator redis;

    public static final String REDIS_ALL_CATEGORY = "redis_all_category";

    /**
     * 解析参数校验错误信息
     */
    public Map<String, String> getErrors(BindingResult result) {
        Map<String, String> map = new HashMap<>();
        List<FieldError> errorList = result.getFieldErrors();
        for (FieldError error : errorList) {
            map.put(error.getField(), error.getDefaultMessage());
        }
        return map;
    }
}