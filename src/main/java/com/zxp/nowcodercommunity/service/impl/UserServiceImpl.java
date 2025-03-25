package com.zxp.nowcodercommunity.service.impl;

import com.zxp.nowcodercommunity.mapper.HomeMapper;
import com.zxp.nowcodercommunity.mapper.UserMapper;
import com.zxp.nowcodercommunity.pojo.User;
import com.zxp.nowcodercommunity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    // 调用来自mapper的接口
    @Autowired
    UserMapper userMapper;
    @Override
    public User getUserById(Integer userId) {
        return userMapper.getUserById(userId);
    }
}

