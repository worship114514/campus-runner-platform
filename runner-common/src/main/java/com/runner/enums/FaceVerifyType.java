package com.runner.enums;

public enum FaceVerifyType {
    BASE64(1, "Base64编码"),
    IMAGE_URL(2, "图片URL");

    public final Integer type;
    public final String value;

    FaceVerifyType(Integer type, String value) {
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