package com.zxp.nowcodercommunity.service;

import java.util.Map;

public interface LoginService {
    Map<String, Object> login(String username, String password, boolean rememberMe);
}
