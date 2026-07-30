package com.runner.enums;

public enum TaskStatus {
    PENDING(1, "待接单"),
    ACCEPTED(2, "进行中"),
    DELIVERED(3, "待确认"),
    COMPLETED(4, "已完成"),
    CANCELLED(5, "已取消");

    public final Integer type;
    public final String value;

    TaskStatus(Integer type, String value) {
        this.type = type;
        this.value = value;
    }

    public Integer getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}