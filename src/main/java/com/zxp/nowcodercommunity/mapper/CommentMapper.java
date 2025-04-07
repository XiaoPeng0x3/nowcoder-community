package com.zxp.nowcodercommunity.mapper;

import com.zxp.nowcodercommunity.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    /**
     *  根据评论的类型进行查询
     *  评论可以分为 对帖子的评论，对用户的评论等等
     */
    List<Comment> selectCommentsByEntity(@Param("entityId") int entityId, @Param("entityType") int entityType,
                                         @Param("offset") int offset, @Param("limit") int limit);

    /**
     *  根据条件查询数量
     */
    Integer selectCountByEntity(int entityId, int entityType);
}
