package com.zxp.nowcodercommunity.mapper;

import com.zxp.nowcodercommunity.pojo.DiscussPost;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HomeMapper {
    /**
     * 根据userId查询帖子，帖子可以为空
     * @param userId
     * @param offset
     * @param limit
     * @param orderMode
     * @return
     */
    List<DiscussPost> getDiscussPosts(Integer userId, Integer offset, Integer limit, Integer orderMode);

    Integer getTotalDiscussPosts(Integer userId);
}

