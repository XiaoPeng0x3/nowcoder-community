package com.zxp.nowcodercommunity.controller;

import com.zxp.nowcodercommunity.dto.LikeDto;
import com.zxp.nowcodercommunity.event.EventProducer;
import com.zxp.nowcodercommunity.pojo.Event;
import com.zxp.nowcodercommunity.result.Result;
import com.zxp.nowcodercommunity.security.util.SecurityUtil;
import com.zxp.nowcodercommunity.service.LikeService;
import org.springframework.web.bind.annotation.*;

import static com.zxp.nowcodercommunity.constant.LoginConstant.TOPIC_LIKE;

@RestController
@RequestMapping("like")
public class LikeController {

    // 注入LikeService服务
    private final LikeService likeService;
    private final EventProducer eventProducer;

    public LikeController(LikeService likeService, EventProducer eventProducer) {
        this.likeService = likeService;
        this.eventProducer = eventProducer;
    }

    @PostMapping
    public Result<Object> like(@RequestBody LikeDto likeDto,
                               @RequestParam("postId") int postId) {
        // TODO Create Time 字段为空


        // 点赞
        likeService.like(SecurityUtil.getUserId(), likeDto.getEntityType(), likeDto.getEntityId(), likeDto.getEntityUserId());
        // 数量
        likeService.findEntityLikeCount(likeDto.getEntityType(), likeDto.getEntityId());
        // 状态
        int entityLikeStatus = likeService.findEntityLikeStatus(SecurityUtil.getUserId(), likeDto.getEntityType(), likeDto.getEntityId());

        // 触发点赞
        if (entityLikeStatus == 1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(SecurityUtil.getUserId())
                    .setEntityType(likeDto.getEntityType())
                    .setEntityId(likeDto.getEntityId())
                    .setEntityUserId(likeDto.getEntityUserId())
                    .setData("postId", postId);

            eventProducer.fireEvent(event);
        }

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
