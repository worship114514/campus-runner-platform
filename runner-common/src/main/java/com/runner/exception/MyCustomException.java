package com.runner.exception;

import com.runner.grace.result.ResponseStatusEnum;

public class MyCustomException extends RuntimeException {
    private ResponseStatusEnum responseStatus;

    public MyCustomException(ResponseStatusEnum responseStatus) {
        super("异常状态码: " + responseStatus.status() + "; 异常信息: " + responseStatus.msg());
        this.responseStatus = responseStatus;
    }

    public ResponseStatusEnum getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(ResponseStatusEnum responseStatus) {
        this.responseStatus = responseStatus;
    }
}