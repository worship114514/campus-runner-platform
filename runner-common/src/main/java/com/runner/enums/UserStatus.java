package com.runner.enums;

public enum UserStatus {
    ACTIVE(1, "正常"),
    FROZEN(2, "已冻结"),
    PENDING(3, "审核中");

    public final Integer type;
    public final String value;

    UserStatus(Integer type, String value) {
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