package com.zxp.nowcodercommunity.dto;

public class LoginDto {
    public LoginDto() {}

    public LoginDto(String username, String password, String code, boolean rememberMe) {
        this.username = username;
        this.password = password;
        this.code = code;
        this.rememberMe = rememberMe;
    }

    private String username; // 用户名

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    private String password; // 密码
    private String code; // 验证码
    private boolean rememberMe; // 记住我选项
}
