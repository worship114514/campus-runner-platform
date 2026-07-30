package com.runner.pojo.bo;

import javax.validation.constraints.NotBlank;

public class RegisterLoginBO {

    @NotBlank(message = "手机号不能为空", groups = {LoginGroup.class, RegisterGroup.class})
    private String mobile;

    @NotBlank(message = "短信验证码不能为空", groups = LoginGroup.class)
    private String smsCode;

    @NotBlank(message = "密码不能为空", groups = RegisterGroup.class)
    private String password;

    // 注册时不需要 userRole，去掉 @NotNull
    private Integer userRole;

    @NotBlank(message = "昵称不能为空", groups = RegisterGroup.class)
    private String nickname;

    private String realName;

    private String idCard;

    // ========== getter/setter ==========
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getUserRole() {
        return userRole;
    }

    public void setUserRole(Integer userRole) {
        this.userRole = userRole;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    // ========== 分组接口 ==========
    public interface LoginGroup {}
    public interface RegisterGroup {}
}