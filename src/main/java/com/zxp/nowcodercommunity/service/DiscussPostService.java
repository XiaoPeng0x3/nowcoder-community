package com.zxp.nowcodercommunity.service;

import com.zxp.nowcodercommunity.pojo.DiscussPost;

import java.util.List;

public interface DiscussPostService {

    Integer addDiscussPost(DiscussPost post);

    DiscussPost findPostById(Integer id);

    Integer findCountById(int userId);

    List<DiscussPost> findPostsById(int userId, int current, int limit, int orderMode);

    Integer updatePostType(Integer userId, Integer type);

    Integer updateStatus(int id, byte status);
}
