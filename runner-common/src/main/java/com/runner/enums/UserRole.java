package com.runner.enums;

public enum UserRole {
    USER(1, "普通用户"),
    RUNNER(2, "跑腿员"),
    ADMIN(3, "管理员");

    public final Integer type;
    public final String value;

    UserRole(Integer type, String value) {
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