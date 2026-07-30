package com.runner.exception;

import com.runner.grace.result.ResponseStatusEnum;

public class GraceException {
    public static void display(ResponseStatusEnum responseStatus) {
        throw new MyCustomException(responseStatus);
    }
}