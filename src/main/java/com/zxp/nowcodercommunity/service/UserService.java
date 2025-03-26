package com.zxp.nowcodercommunity.service;

import com.zxp.nowcodercommunity.pojo.User;

public interface UserService {

    User getUserById(Integer userId);

    String registerUser(User user);

}
