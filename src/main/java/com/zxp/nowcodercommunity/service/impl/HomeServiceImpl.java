package com.zxp.nowcodercommunity.service.impl;

import com.zxp.nowcodercommunity.mapper.HomeMapper;
import com.zxp.nowcodercommunity.pojo.DiscussPost;
import com.zxp.nowcodercommunity.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    private HomeMapper homeMapper;

    @Override
    public List<DiscussPost> getDiscussPosts(int userId, int current, int limit, int orderMode) {
        int offset = (current - 1) * limit;
        List<DiscussPost> discussPosts = homeMapper.getDiscussPosts(userId, offset, limit, orderMode); // 返回前端
        return discussPosts;
    }

    @Override
    public int getTotalDiscussPosts(int userId) {
        return homeMapper.getTotalDiscussPosts(userId);
    }
}

