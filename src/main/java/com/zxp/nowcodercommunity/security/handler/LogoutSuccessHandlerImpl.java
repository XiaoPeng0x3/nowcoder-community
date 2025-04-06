package com.zxp.nowcodercommunity.security.handler;

import com.alibaba.fastjson2.JSONObject;
import com.zxp.nowcodercommunity.result.Result;
import com.zxp.nowcodercommunity.security.model.LoginUser;
import com.zxp.nowcodercommunity.util.JwtUtil;
import com.zxp.nowcodercommunity.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
    private final JwtUtil jwtUtil;

    public LogoutSuccessHandlerImpl(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 删除jwt

        // 得到token
        LoginUser loginUser = jwtUtil.getLoginUser(request);
        if (loginUser != null) {
            // 删除token
            jwtUtil.removeToken(loginUser.getToken());
        }
        WebUtil.renderString(response, JSONObject.toJSONString(Result.success().message("退出成功")));

    }
}
