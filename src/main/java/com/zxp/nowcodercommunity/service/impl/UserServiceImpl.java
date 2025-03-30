package com.zxp.nowcodercommunity.service.impl;

import com.zxp.nowcodercommunity.constant.RegisterConstants;
import com.zxp.nowcodercommunity.mapper.UserMapper;
import com.zxp.nowcodercommunity.pojo.User;
import com.zxp.nowcodercommunity.service.UserService;
import com.zxp.nowcodercommunity.util.CommunityUtil;
import com.zxp.nowcodercommunity.util.EmailSendService;
import com.zxp.nowcodercommunity.util.check.UserCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Random;

@Service
public class UserServiceImpl implements UserService {
    // 调用来自mapper的接口
    private final UserMapper userMapper;

    private final TemplateEngine templateEngine;

    private final EmailSendService emailSendService;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public UserServiceImpl(UserMapper userMapper, TemplateEngine templateEngine, EmailSendService emailSendService) {
        this.userMapper = userMapper;
        this.templateEngine = templateEngine;
        this.emailSendService = emailSendService;
    }

    @Override
    public User getUserById(Integer userId) {
        return userMapper.getUserById(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)  // 指定所有异常都回滚
    public String registerUser(User user) {
        // 首先对user进行各种参数检查
        if (!UserCheck.checkUser(user)) {
            throw new IllegalArgumentException(RegisterConstants.USER_NULL);
        }

        if (!UserCheck.checkPassword(user)) {
            // password为空
            return RegisterConstants.USER_PASSWORD_NULL;
        }

        if (!UserCheck.checkEmail(user)) {
            // email为空
            return RegisterConstants.USER_EMAIL_NULL;
        }

        // 判断用户的用户名是否已经存在
        User userByUsername = userMapper.selectUserByUsername(user.getUsername());
        if (userByUsername != null) {
            return RegisterConstants.USER_NAME_EXIST;
        }

        // 判断用户的email是否已经被注册
        User userByEmail = userMapper.selectUserByEmail(user.getEmail());
        if (userByEmail != null) {
            return RegisterConstants.USER_EMAIL_EXIST;
        }

        // 注册用户
        // 将数据封装后传递到mapper层
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        // 设置密码
        // 密码为md5(password + salt)
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt())); // 覆盖password
        // 设置用户类型
        user.setType(RegisterConstants.USER_COMMON); // 普通用户
        // 设置用户激活状态
        user.setStatus(RegisterConstants.USER_DEACTIVATE); // 未激活
        // 设置激活码
        user.setActivationCode(CommunityUtil.generateUUID());
        // 设置随机头像
        user.setHeaderUrl(String.format("https://images.nowcoder.com/head/%dt.png", new Random().nextInt(0, 1000)));

        // 还有一个字段就是创建的时间createTime
        // 把这个字段写到aop方法里面
        // user.setCreateTime();
        userMapper.insertUser(user);

        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());

        String url = contextPath + domain + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);

        // 模板引擎生成模板内容
        String html = templateEngine.process("/mail/activation", context);

        // 发送邮件
        emailSendService.sendSimpleMail(user.getEmail(), "激活账号", html);

        return RegisterConstants.SUCCESS;
    }
}

