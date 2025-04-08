package com.zxp.nowcodercommunity.service.impl;

import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import com.zxp.nowcodercommunity.mapper.CommentMapper;
import com.zxp.nowcodercommunity.mapper.DiscussPostMapper;
import com.zxp.nowcodercommunity.pojo.Comment;
import com.zxp.nowcodercommunity.pojo.DiscussPost;
import com.zxp.nowcodercommunity.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.zxp.nowcodercommunity.constant.LoginConstant.ENTITY_TYPE_COMMENT;
import static com.zxp.nowcodercommunity.constant.LoginConstant.ENTITY_TYPE_POST;

@Service
public class CommentServiceImpl implements CommentService {

    private static final Logger log = LoggerFactory.getLogger(CommentServiceImpl.class);
    // 注入mapper
    private final CommentMapper commentMapper;
    private final DiscussPostMapper discussPostMapper;

    public CommentServiceImpl(CommentMapper commentMapper, DiscussPostMapper discussPostMapper) {
        this.commentMapper = commentMapper;
        this.discussPostMapper = discussPostMapper;
    }

    @Override
    public List<Comment> findCommentsByEntity(int entityId, int entityType, int offset, int limit) {
        List<Comment> ans = new ArrayList<>();
        try {
            ans = commentMapper.selectCommentsByEntity(entityId, entityType, offset, limit);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.info("findCommentsByEntity:{}", ans);
        return ans;
    }

    @Override
    public Integer findCommentCountByEntity(int entityId, int entityType) {
        return commentMapper.selectCountByEntity(entityId, entityType);
    }
    @Override
    public Integer insertComment(Comment comment) {
        // 将comment中的敏感词过滤
        comment.setContent(SensitiveWordHelper.replace(comment.getContent(), '*'));


        // 调用mapper层的接口



        if (comment.getEntityType().equals(ENTITY_TYPE_POST)) { // 回帖，评论的是帖子
            // 帖子的评论数量要+1
            Integer count = commentMapper.selectCountByEntity(comment.getEntityId(), comment.getEntityType());

            discussPostMapper.updateCommentCount(comment.getEntityId(), count + 1);
        }
        return commentMapper.insertComment(comment);
    }
}
