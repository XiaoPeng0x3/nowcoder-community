package com.zxp.nowcodercommunity.controller;

import com.zxp.nowcodercommunity.dto.FollowDto;
import com.zxp.nowcodercommunity.event.EventProducer;
import com.zxp.nowcodercommunity.pojo.Event;
import com.zxp.nowcodercommunity.pojo.Page;
import com.zxp.nowcodercommunity.result.Result;
import com.zxp.nowcodercommunity.security.util.SecurityUtil;
import com.zxp.nowcodercommunity.service.FollowService;
import com.zxp.nowcodercommunity.service.UserService;
import org.springframework.web.bind.annotation.*;

import static com.zxp.nowcodercommunity.constant.LoginConstant.ENTITY_TYPE_USER;
import static com.zxp.nowcodercommunity.constant.LoginConstant.TOPIC_FOLLOW;

@RestController
public class FollowController {

    private final FollowService followService;
    private final UserService userService;
    private final EventProducer eventProducer;

    public FollowController(FollowService followService, UserService userService, EventProducer eventProducer) {
        this.followService = followService;
        this.userService = userService;
        this.eventProducer = eventProducer;
    }

    /**
     *  关注
     * @param followDto
     * @return
     */
    @PostMapping("follow")
    public Result<Object> follow(@RequestBody FollowDto followDto) {
        // 执行关注
        followService.follow(SecurityUtil.getUserId(), followDto.getEntityId(), followDto.getEntityType());
        // 触发关注事件
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setEntityId(followDto.getEntityId())
                .setEntityType(followDto.getEntityType())
                .setUserId(SecurityUtil.getUserId())
                .setEntityUserId(followDto.getEntityId());
        eventProducer.fireEvent(event);

        return Result.success();
    }

    /**
     *  取消关注
     * @param followDto
     * @return
     */
    @PostMapping("unfollow")
    public Result<Object> unfollow(@RequestBody FollowDto followDto) {
        followService.unfollow(SecurityUtil.getUserId(), followDto.getEntityId(), followDto.getEntityType());
        return Result.success();
    }

    @GetMapping("follow")
    public Result<Boolean> followed(@RequestParam int entityType, @RequestParam int entityId) {
        return Result.success(followService.followed(SecurityUtil.getUserId(), entityType, entityId));
    }

    @GetMapping("followee/count")
    public Result<Integer> followeeCount(@RequestParam int userId, @RequestParam int entityType) {
        return Result.success(followService.findFolloweeCount(userId, entityType));
    }

    @GetMapping("follower/count")
    public Result<Integer> followerCount(@RequestParam int entityType, @RequestParam int entityId) {
        return Result.success(followService.findFollowerCount(entityType, entityId));
    }

    /**
     * 关注列表
     * @param userId
     * @param current
     * @param limit
     * @return
     */
    @GetMapping("followee/{userId}")
    public Result<Object> getFollowees(@PathVariable int userId,
                                       @RequestParam("current") int current,
                                       @RequestParam("limit") int limit) {

        if (userService.getUserById(userId) == null) {
            return Result.error().message("该用户不存在！");
        }

        Page page = new Page();
        page.setCurrent(current);
        page.setLimit(limit);

        page.setRows(followService.findFolloweeCount(userId, (int) ENTITY_TYPE_USER));

        return Result.success(followService.findFollowees(userId, page.getOffset(), page.getLimit()));
    }

    /**
     * 粉丝列表
     * @param userId
     * @param current
     * @param limit
     * @return
     */
    @GetMapping("follower/{userId}")
    public Result<Object> getFollowers(@PathVariable int userId,
                                       @RequestParam("current") int current,
                                       @RequestParam("limit") int limit) {

        if (userService.getUserById(userId) == null) {
            return Result.error().message("该用户不存在！");
        }
        Page page = new Page();
        page.setCurrent(current);
        page.setLimit(limit);
        page.setRows(followService.findFollowerCount((int) ENTITY_TYPE_USER, userId));

        return Result.success(followService.findFollowers(userId, page.getOffset(), page.getLimit()));
    }
}
