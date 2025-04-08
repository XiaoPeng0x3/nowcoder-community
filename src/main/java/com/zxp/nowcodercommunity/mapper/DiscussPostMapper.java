package com.zxp.nowcodercommunity.mapper;

import com.zxp.nowcodercommunity.pojo.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    // 向数据库里面插入post
    Integer addDiscussPost(DiscussPost discussPost);

    DiscussPost findPostById(@Param("id") Integer id);

    Integer findCountById(int userId);

    List<DiscussPost> findPostsById(int userId, int offset, int limit, int orderMode);

    Integer updatePostType(Integer id, Integer type);

    Integer updateStatus(Integer id, byte status);

    void updateCommentCount(Integer id, Integer commentCount);
}
