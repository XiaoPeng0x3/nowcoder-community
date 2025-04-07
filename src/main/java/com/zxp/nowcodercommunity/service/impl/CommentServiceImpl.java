package com.zxp.nowcodercommunity.service.impl;

import com.zxp.nowcodercommunity.mapper.CommentMapper;
import com.zxp.nowcodercommunity.pojo.Comment;
import com.zxp.nowcodercommunity.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    // 注入mapper
    private final CommentMapper commentMapper;
    public CommentServiceImpl(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    @Override
    public List<Comment> findCommentsByEntity(int entityId, int entityType, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityId, entityType, offset, limit);
    }

    @Override
    public Integer findCommentCountByEntity(int entityId, int entityType) {
        return commentMapper.selectCountByEntity(entityId, entityType);
    }
}
