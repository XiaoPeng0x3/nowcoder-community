package com.zxp.nowcodercommunity.service.impl;

import com.zxp.nowcodercommunity.service.LoginService;


import java.util.HashMap;
import java.util.Map;

public class LoginServiceImpl implements LoginService {

    /**
     * 实现登录验证的功能
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
        return null;
    }
}
