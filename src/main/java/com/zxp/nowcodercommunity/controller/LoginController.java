package com.zxp.nowcodercommunity.controller;

import com.google.code.kaptcha.Producer;
import com.zxp.nowcodercommunity.constant.RegisterConstants;
import com.zxp.nowcodercommunity.dto.LoginDto;
import com.zxp.nowcodercommunity.pojo.User;
import com.zxp.nowcodercommunity.result.Result;
import com.zxp.nowcodercommunity.service.UserService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse; // SpringBoot3.0以上应该使用jakarta
import jakarta.servlet.http.HttpSession; // SpringBoot3.0以上应该使用jakarta
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping
public class LoginController {

    private final UserService userService;
    private final Producer kaptchaProducer; // 注入验证码生成器

    public LoginController(UserService userService, Producer kaptchaProducer) {
        this.userService = userService;
        this.kaptchaProducer = kaptchaProducer;
    }

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
        // TODO
        // 将验证码存入redis里面
        BufferedImage image = kaptchaProducer.createImage(text); // 生成图片
        // 将图片存在session
        session.setAttribute("kaptcha", text);
        // 输入图片给浏览器
        //response.setContentType("img/png");

        // TODO
        // 给请求验证码的接口加流量限制

        try {
            ServletOutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            System.out.println("响应验证码失败");
        }
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginDto loginDto, @CookieValue(value = "kaptcha", required = false) String kaptcha) {
        // 将传递过来的验证码进行对比比较
        Map<String, Object> data = new HashMap<>();

        if (StringUtils.isBlank(kaptcha)) {
            data.put("codeMsg", "验证码错误");
            return Result.error(data);
        }
        // TODO
        // 验证码要从redis里面取出
        // 前端传递过来的验证码为空或者前端传递的验证码与存储在cookie里面的验证码不一致
        if (StringUtils.isBlank(loginDto.getCode()) ||
        loginDto.getCode().equalsIgnoreCase(kaptcha)) {
            data.put("codeMsg", "验证码错误");
            return Result.error(data);
        }
        return null;

    }
}
