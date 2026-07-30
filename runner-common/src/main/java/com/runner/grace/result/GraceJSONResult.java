package com.runner.grace.result;

import java.util.Map;

public class GraceJSONResult {
    private Integer status;
    private String msg;
    private Boolean success;
    private Object data;

    public static GraceJSONResult ok(Object data) {
        return new GraceJSONResult(data);
    }

    public static GraceJSONResult ok() {
        return new GraceJSONResult(ResponseStatusEnum.SUCCESS);
    }

    public static GraceJSONResult error() {
        return new GraceJSONResult(ResponseStatusEnum.FAILED);
    }

    public static GraceJSONResult errorMap(Map map) {
        return new GraceJSONResult(ResponseStatusEnum.FAILED, map);
    }

    public static GraceJSONResult errorMsg(String msg) {
        return new GraceJSONResult(ResponseStatusEnum.FAILED, msg);
    }

    public static GraceJSONResult errorTicket() {
        return new GraceJSONResult(ResponseStatusEnum.TICKET_INVALID);
    }

    public static GraceJSONResult errorCustom(ResponseStatusEnum responseStatus) {
        return new GraceJSONResult(responseStatus);
    }

    public GraceJSONResult(Object data) {
        this.status = ResponseStatusEnum.SUCCESS.status();
        this.msg = ResponseStatusEnum.SUCCESS.msg();
        this.success = ResponseStatusEnum.SUCCESS.success();
        this.data = data;
    }

    public GraceJSONResult(ResponseStatusEnum responseStatus) {
        this.status = responseStatus.status();
        this.msg = responseStatus.msg();
        this.success = responseStatus.success();
    }

    public GraceJSONResult(ResponseStatusEnum responseStatus, Object data) {
        this.status = responseStatus.status();
        this.msg = responseStatus.msg();
        this.success = responseStatus.success();
        this.data = data;
    }

    public GraceJSONResult(ResponseStatusEnum responseStatus, String msg) {
        this.status = responseStatus.status();
        this.msg = msg;
        this.success = responseStatus.success();
    }

    public GraceJSONResult() {
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}