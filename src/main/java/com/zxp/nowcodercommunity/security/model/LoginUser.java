package com.zxp.nowcodercommunity.security.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zxp.nowcodercommunity.pojo.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.zxp.nowcodercommunity.constant.LoginConstant.*;
import static com.zxp.nowcodercommunity.constant.RegisterConstants.USER_ADMIN;
import static com.zxp.nowcodercommunity.constant.RegisterConstants.USER_COMMON;

@JsonIgnoreProperties({"username", "password", "enabled","accountNonExpired", "accountNonLocked", "credentialsNonExpired", "authorities"})
public class LoginUser implements UserDetails, Serializable {
    /**
     * 用户
     */
    private User user;
    /**
     * token标识
     */
    private String token;
    /**
     * 登陆时间
     */
    private Long loginTime;
    /**
     * 过期时间
     */
    private Long expireTime;

    public LoginUser(User user, String token, Long loginTime, Long expireTime) {
        this.user = user;
        this.token = token;
        this.loginTime = loginTime;
        this.expireTime = expireTime;
    }

    public LoginUser() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user == null) {
            return authorities;
        }
        // 从登录用户中获取用户的权限
        String role;
        if (user != null && user.getType() == USER_COMMON) { // 普通用户
            role = AUTHORITY_USER;
        } else if (user != null && user.getType() == USER_ADMIN) {
            role = AUTHORITY_ADMIN;
        } else {
            role = AUTHORITY_MODERATOR;
        }
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }
}
