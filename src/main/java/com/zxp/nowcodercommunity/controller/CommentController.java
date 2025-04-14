package com.zxp.nowcodercommunity.controller;

import com.zxp.nowcodercommunity.annotation.AutoCreateTime;
import com.zxp.nowcodercommunity.event.EventProducer;
import com.zxp.nowcodercommunity.pojo.Comment;
import com.zxp.nowcodercommunity.pojo.DiscussPost;
import com.zxp.nowcodercommunity.pojo.Event;
import com.zxp.nowcodercommunity.pojo.Page;
import com.zxp.nowcodercommunity.result.Result;
import com.zxp.nowcodercommunity.security.util.SecurityUtil;
import com.zxp.nowcodercommunity.service.CommentService;
import com.zxp.nowcodercommunity.service.DiscussPostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.zxp.nowcodercommunity.constant.LoginConstant.*;

@RestController
public class CommentController {

    /**
     *  添加评论
     *  思路
     *  - 因为评论有两种，一种是该帖子的评论，另一种是回复，即用户之间的回复，所以复杂就复杂在这里
     */

    private final CommentService commentService;
    private final DiscussPostService discussPostService;
    private final EventProducer eventProducer;

    public CommentController(CommentService commentService, DiscussPostService discussPostService, EventProducer eventProducer) {

        this.commentService = commentService;
        this.discussPostService = discussPostService;
        this.eventProducer = eventProducer;
    }

    /**
     *  在引入kafka后，就可以在添加品论的时候给用户发送一个消息，来通知用户收到了{点赞、评论、关注}事件
     * @param comment
     * @return
     */
    @AutoCreateTime
    @PostMapping("comment/add")
    public Result<Object> addComment(@RequestBody Comment comment) {
        // 谁发的这个评论
        Integer userId = SecurityUtil.getUserId();
        comment.setUserId(userId);
        // 评论不被删除
        comment.setStatus((byte)0);
        // TODO 回复评论的评论
        Integer rows = commentService.insertComment(comment);
        // 再插入帖子后就可以进行回复

        // 发送消息
        // 触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(userId)
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", comment.getEntityId());
        // 设置 event的UserId字段
        if (comment.getEntityType() == ENTITY_TYPE_POST) { // 评论的对象是帖子
            // 找到帖子的userId
            DiscussPost postById = discussPostService.findPostById(comment.getEntityId());
            event.setEntityUserId(postById.getUserId());
        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) { // 评论的是评论
            // 根据entityId找到comment
            Comment commentById = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(commentById.getUserId());
        }


        // 处理事件
        eventProducer.fireEvent(event);

        if (rows < 0) {
            Result.error("更新失败");
        }
        return Result.success(rows);
    }
}
