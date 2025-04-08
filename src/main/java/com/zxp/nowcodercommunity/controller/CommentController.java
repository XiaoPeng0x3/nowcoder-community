package com.zxp.nowcodercommunity.controller;

import com.zxp.nowcodercommunity.annotation.AutoCreateTime;
import com.zxp.nowcodercommunity.pojo.Comment;
import com.zxp.nowcodercommunity.pojo.Page;
import com.zxp.nowcodercommunity.result.Result;
import com.zxp.nowcodercommunity.security.util.SecurityUtil;
import com.zxp.nowcodercommunity.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.zxp.nowcodercommunity.constant.LoginConstant.ENTITY_TYPE_POST;

@RestController
public class CommentController {

    /**
     *  添加评论
     *  思路
     *  - 因为评论有两种，一种是该帖子的评论，另一种是回复，即用户之间的回复，所以复杂就复杂在这里
     */

    private final CommentService commentService;
    public CommentController(CommentService commentService) {

        this.commentService = commentService;
    }


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


        if (rows < 0) {
            Result.error("更新失败");
        }
        return Result.success(rows);
    }
}
