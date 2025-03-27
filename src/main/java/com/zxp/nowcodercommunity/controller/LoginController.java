package com.zxp.nowcodercommunity.controller;

import com.google.code.kaptcha.Producer;
import com.zxp.nowcodercommunity.constant.RegisterConstants;
import com.zxp.nowcodercommunity.pojo.User;
import com.zxp.nowcodercommunity.result.Result;
import com.zxp.nowcodercommunity.service.UserService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse; // SpringBoot3.0以上应该使用jakarta
import jakarta.servlet.http.HttpSession; // SpringBoot3.0以上应该使用jakarta
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;


@RestController
@RequestMapping
public class LoginController {
    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer; // 注入验证码生成器

    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        String message = userService.registerUser(user);
        if (message.equals(RegisterConstants.USER_EMAIL_EXIST) || message.equals(RegisterConstants.USER_NAME_EXIST)) {
            return Result.error(409, message);
        }
        return Result.success(message);
    }

    @GetMapping(value = "/kaptcha", produces = MediaType.IMAGE_PNG_VALUE)
    public void getKaptcha(HttpServletResponse response, HttpSession session) throws IOException {
        // 生成验证码
        String text = kaptchaProducer.createText(); // 生成验证码
        BufferedImage image = kaptchaProducer.createImage(text); // 生成图片
        // 将图片存在session
        session.setAttribute("kaptcha", text);
        // 输入图片给浏览器
        //response.setContentType("img/png");

        // TODO
        // 给请求验证码的接口加

        try {
            ServletOutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            System.out.println("响应验证码失败");
        }
    }
}
