package com.zxp.nowcodercommunity.controller;

import com.zxp.nowcodercommunity.dto.LikeDto;
import com.zxp.nowcodercommunity.result.Result;
import com.zxp.nowcodercommunity.security.util.SecurityUtil;
import com.zxp.nowcodercommunity.service.LikeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("like")
public class LikeController {

    // 注入LikeService服务
    private final LikeService likeService;
    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping
    public Result<Object> like(@RequestBody LikeDto likeDto,
                               @RequestParam("postId") int postId) {

        // 点赞
        likeService.like(SecurityUtil.getUserId(), likeDto.getEntityType(), likeDto.getEntityId(), likeDto.getEntityUserId());
        // 数量
        likeService.findEntityLikeCount(likeDto.getEntityType(), likeDto.getEntityId());
        // 状态
        likeService.findEntityLikeStatus(SecurityUtil.getUserId(), likeDto.getEntityType(), likeDto.getEntityId());

        return Result.success();

    }

    @GetMapping("count")
    public Result<Long> likeCount(@RequestParam int entityType,
                                  @RequestParam int entityId) {
        return Result.success(likeService.findEntityLikeCount(entityType, entityId));
    }

    @GetMapping("status")
    public Result<Integer> likeStatus(@RequestParam int entityType,
                                   @RequestParam int entityId) {
        return Result.success(likeService.findEntityLikeStatus(SecurityUtil.getUserId(), entityType, entityId));
    }

}
