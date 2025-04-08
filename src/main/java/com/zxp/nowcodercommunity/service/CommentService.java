package com.zxp.nowcodercommunity.service;

import com.zxp.nowcodercommunity.pojo.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> findCommentsByEntity(int entityId, int entityType, int offset, int limit);

    Integer findCommentCountByEntity(int entityId, int entityType);

    Integer insertComment(Comment comment);
}
