package com.zxp.nowcodercommunity.controller;

import com.google.code.kaptcha.Producer;
import com.zxp.nowcodercommunity.constant.RegisterConstants;
import com.zxp.nowcodercommunity.dto.LoginDto;
import com.zxp.nowcodercommunity.pojo.User;
import com.zxp.nowcodercommunity.result.Result;
import com.zxp.nowcodercommunity.service.LoginService;
import com.zxp.nowcodercommunity.service.UserService;
import com.zxp.nowcodercommunity.util.CommunityUtil;
import com.zxp.nowcodercommunity.util.RedisCache;
import com.zxp.nowcodercommunity.util.RedisKeyUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse; // SpringBoot3.0以上应该使用jakarta
import jakarta.servlet.http.HttpSession; // SpringBoot3.0以上应该使用jakarta
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private final UserService userService;
    private final Producer kaptchaProducer; // 注入验证码生成器
    private final RedisCache redisCache;
    private final LoginService loginService;

    // cookie的path
    @Value("${spring.servlet.session.cookie.path}")
    private String cookiePath;

    public LoginController(UserService userService, Producer kaptchaProducer, RedisCache redisCache, LoginService loginService) {
        this.userService = userService;
        this.kaptchaProducer = kaptchaProducer;
        this.redisCache = redisCache;
        this.loginService = loginService;
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
        // session.setAttribute("kaptcha", text);

        // 将验证码存放在redis里面
        // 验证码的owner
        // 将这个凭证存放在cookie里面
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptcha", kaptchaOwner);
        cookie.setMaxAge(60); // 一分钟
        cookie.setPath(cookiePath);
        // 将cookie发送给客户端
        response.addCookie(cookie);

        // 将验证码存储redis
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisCache.setCacheObject(redisKey, text, 60, TimeUnit.SECONDS); // 设置redis的有效期为60s


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

    /**
     *  登录的时候，需要将前端传递过来的验证码参数与存在redis里面的验证码做对比
     * @param loginDto
     * @param kaptcha
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginDto loginDto, @CookieValue(value = "kaptcha", required = false) String kaptcha) {
        // 将传递过来的验证码进行对比比较
        log.info("开始进行login controller调用loginDto={}", loginDto);
        Map<String, Object> data = new HashMap<>();

        if (StringUtils.isBlank(kaptcha)) {
            data.put("codeMsg", "验证码错误");
            return Result.error(data);
        }
        // 验证码要从redis里面取出
        String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptcha);
        // 从redisCache里面取出
        String redisKaptchKey = redisCache.getCacheObject(kaptchaKey);
        // 前端传递过来的验证码为空或者前端传递的验证码与存储在cookie里面的验证码不一致
        if ( StringUtils.isBlank(kaptcha) || // cookie 空
                StringUtils.isBlank(loginDto.getCode()) || // 传递的验证码为空
                StringUtils.isBlank(redisKaptchKey) || // redis里面没有这个验证码
                !redisKaptchKey.equalsIgnoreCase(loginDto.getCode()) // redis里面的验证码和前端传递过来的不一致
        ) {
            data.put("codeMsg", "验证码错误");
            return Result.error(data);
        }
        // 检查账号密码
        // service层里面的内容
        data = loginService.login(loginDto.getUsername(), loginDto.getPassword(), loginDto.isRememberMe());
        return Result.success(data);

    }
}
