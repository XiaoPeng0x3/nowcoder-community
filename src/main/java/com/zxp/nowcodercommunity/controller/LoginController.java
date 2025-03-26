package com.zxp.nowcodercommunity.controller;

import com.zxp.nowcodercommunity.constant.RegisterConstants;
import com.zxp.nowcodercommunity.pojo.User;
import com.zxp.nowcodercommunity.result.Result;
import com.zxp.nowcodercommunity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class LoginController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        String message = userService.registerUser(user);
        if (message.equals(RegisterConstants.USER_EMAIL_EXIST) || message.equals(RegisterConstants.USER_NAME_EXIST)) {
            return Result.error(409, message);
        }
        return Result.success(message);
    }
}
