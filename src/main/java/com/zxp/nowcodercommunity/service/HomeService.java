package com.zxp.nowcodercommunity.service;

import com.zxp.nowcodercommunity.mapper.HomeMapper;
import com.zxp.nowcodercommunity.pojo.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 业务逻辑
 */
public interface HomeService {

    List<DiscussPost> getDiscussPosts(int userId, int current, int limit, int orderMode);

    // 查询帖子总数
    int getTotalDiscussPosts(int userId);
}


