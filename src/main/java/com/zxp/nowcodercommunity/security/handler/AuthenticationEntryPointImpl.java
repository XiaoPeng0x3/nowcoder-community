package com.zxp.nowcodercommunity.security.handler;

import com.alibaba.fastjson2.JSONObject;
import com.zxp.nowcodercommunity.result.Result;
import com.zxp.nowcodercommunity.util.ResultCodeEnum;
import com.zxp.nowcodercommunity.util.WebUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        // 没有登录时
        WebUtil.renderString(response, JSONObject.toJSONString(Result.error().code(ResultCodeEnum.FORBIDDEN.getCode()).message("你还没有登录哦！")));
    }
}
