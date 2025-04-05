package com.zxp.nowcodercommunity.security.core;

import com.zxp.nowcodercommunity.mapper.UserMapper;
import com.zxp.nowcodercommunity.pojo.User;
import com.zxp.nowcodercommunity.security.model.LoginUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 1. 重写方法
 * 2. 将方法注入到容器中
 * 3. 这样spring security就会去数据库查询
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    /**
     * 注入mapper
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */

    private UserMapper userMapper;

    public UserDetailServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectUserByUsername(username);
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user); // 将用户信息封装进去
        return loginUser;
    }
}
