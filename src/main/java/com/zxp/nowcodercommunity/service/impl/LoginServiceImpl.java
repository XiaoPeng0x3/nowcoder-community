package com.zxp.nowcodercommunity.service.impl;

import com.zxp.nowcodercommunity.pojo.User;
import com.zxp.nowcodercommunity.security.core.AuthenticationContextHolder;
import com.zxp.nowcodercommunity.security.model.LoginUser;
import com.zxp.nowcodercommunity.service.LoginService;
import com.zxp.nowcodercommunity.util.JwtUtil;
import com.zxp.nowcodercommunity.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public LoginServiceImpl(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }


    /**
     * 实现登录验证的功能
     * 需要实现以下几个功能
     * 1. 首先从spring security中接受对象
     * @param username
     * @param password
     * @param rememberMe
     * @return
     */
    @Override
    public Map<String, Object> login(String username, String password, boolean rememberMe) {
        Map<String, Object> result = new HashMap<>();
        // 开始进行登录验证
        // 认证是通过AuthenticationManger的authenticate函数实现的

        // 封装前端传递过来的对象
        Authentication auth;

        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password); // 可能会引起找不到User的异常
            AuthenticationContextHolder.setAuthentication(token); // 存储上下文
            // 会自动调用loadUserByUserName方法
            auth = authenticationManager.authenticate(token); // 将这个信息存储在上下文里面
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("用户名或密码错误");
        } catch (DisabledException e) {
            throw new DisabledException("账号已被禁用");
        } catch (LockedException e) {
            throw new LockedException("账号已被锁定");
        } catch (AccountExpiredException e) {
            throw new AccountExpiredException("账号已过期");
        } catch (CredentialsExpiredException e) {
            throw new CredentialsExpiredException("密码已过期");
        } catch (Exception e) {
            throw new RuntimeException("登录失败: " + e.getMessage());
        } finally {
            AuthenticationContextHolder.clearAuthentication();
        }

        // 查询返回到的信息
        LoginUser loginUser = (LoginUser) auth.getPrincipal();
        // 生成token
        String token = jwtUtil.createToken(loginUser);
        // 将数据返回
        User user = loginUser.getUser();
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        result.put("token", token);
        result.put("user", userVo);
        return result;
    }
}
