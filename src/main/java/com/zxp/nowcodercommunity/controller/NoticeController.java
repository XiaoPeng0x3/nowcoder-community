package com.zxp.nowcodercommunity.controller;

import com.alibaba.fastjson2.JSONObject;
import com.zxp.nowcodercommunity.pojo.Message;
import com.zxp.nowcodercommunity.pojo.Page;
import com.zxp.nowcodercommunity.pojo.User;
import com.zxp.nowcodercommunity.result.Result;
import com.zxp.nowcodercommunity.security.model.LoginUser;
import com.zxp.nowcodercommunity.security.util.SecurityUtil;
import com.zxp.nowcodercommunity.service.MessageService;
import com.zxp.nowcodercommunity.service.UserService;
import com.zxp.nowcodercommunity.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.zxp.nowcodercommunity.constant.LoginConstant.*;

@RestController
@RequestMapping("notice")
public class NoticeController {

    private final MessageService messageService;
    private final UserService userService;
    public NoticeController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    /**
     * notice显示
     * 通知有：点赞通知、回复通知、关注通知
     */
    @GetMapping("list")
    public Result<Map<String, Object>> getNoticeList() {
        Map<String, Object> notice = new HashMap<>();
        // 查询评论类通知
        Message commentMessage = messageService.findLatestNotice(SecurityUtil.getUserId(), TOPIC_COMMENT);
        Map<String, Object> commentNoticeVo = new HashMap<>();
        if (commentMessage != null) {
            commentNoticeVo.put("message", commentMessage);

            String content = HtmlUtils.htmlUnescape(commentMessage.getContent());
            Map data =  JSONObject.parseObject(content, Map.class);

            User user = userService.getUserById((Integer) data.get("userId"));
            UserVo userVo = new UserVo();
            if (user != null) {
                BeanUtils.copyProperties(user, userVo);
            }
            commentNoticeVo.put("user", userVo);
            commentNoticeVo.put("entityType", data.get("entityType"));
            commentNoticeVo.put("entityId", data.get("entityId"));
            commentNoticeVo.put("postId", data.get("postId"));

            commentNoticeVo.put("count", messageService.findNoticeCount(SecurityUtil.getUserId(), TOPIC_COMMENT));
            commentNoticeVo.put("unreadCount", messageService.findNoticeUnreadCount(SecurityUtil.getUserId(), TOPIC_COMMENT));

            notice.put("commentNotice", commentNoticeVo);
        }
        // 查询点赞类通知
        Message likeMessage = messageService.findLatestNotice(SecurityUtil.getUserId(), TOPIC_LIKE);
        Map<String, Object> likeNoticeVo = new HashMap<>();
        if (likeMessage != null) {
            likeNoticeVo.put("message", likeMessage);

            String content = HtmlUtils.htmlUnescape(likeMessage.getContent());
            Map data = JSONObject.parseObject(content, Map.class);

            User user = userService.getUserById((Integer) data.get("userId"));
            UserVo userVo = new UserVo();
            if (user != null) {
                BeanUtils.copyProperties(user, userVo);
            }
            likeNoticeVo.put("user", userVo);
            likeNoticeVo.put("entityType", data.get("entityType"));
            likeNoticeVo.put("entityId", data.get("entityId"));
            likeNoticeVo.put("postId", data.get("postId"));

            likeNoticeVo.put("count", messageService.findNoticeCount(SecurityUtil.getUserId(), TOPIC_LIKE));
            likeNoticeVo.put("unreadCount", messageService.findNoticeUnreadCount(SecurityUtil.getUserId(), TOPIC_LIKE));

            notice.put("likeNotice", likeNoticeVo);
        }
        // 查询关注类通知
        Message followMessage = messageService.findLatestNotice(SecurityUtil.getUserId(), TOPIC_FOLLOW);
        Map<String, Object> followNoticeVo = new HashMap<>();
        if (followMessage != null) {
            followNoticeVo.put("message", followMessage);

            String content = HtmlUtils.htmlUnescape(followMessage.getContent());
            Map data = JSONObject.parseObject(content, Map.class);

            User user = userService.getUserById((Integer) data.get("userId"));
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user, userVo);
            followNoticeVo.put("user", userVo);
            followNoticeVo.put("entityType", data.get("entityType"));
            followNoticeVo.put("entityId", data.get("entityId"));

            followNoticeVo.put("count", messageService.findNoticeCount(SecurityUtil.getUserId(), TOPIC_FOLLOW));
            followNoticeVo.put("unreadCount", messageService.findNoticeUnreadCount(SecurityUtil.getUserId(), TOPIC_FOLLOW));

            notice.put("followNotice", followNoticeVo);
        }
        return Result.success(notice);
    }

    /**
     * 找到总的未读消息数
     * @return
     */
    @GetMapping("unread/count")
    public Result<Integer> getNoticeUnreadCount() {
        return Result.success(messageService.findNoticeUnreadCount(SecurityUtil.getUserId(), null));
    }

    /**
     * 通知的详情列表展示
     * @param topic
     * @param current
     * @param limit
     * @return
     */
    @GetMapping("detail/{topic}")
    public Result<List<Map<String, Object>>> getNotices(@PathVariable("topic") String topic,
                                                        @RequestParam("current") int current,
                                                        @RequestParam("limit") int limit) {
        Page page = new Page();
        page.setCurrent(current);
        page.setLimit(limit);
        page.setRows(messageService.findNoticeCount(SecurityUtil.getUserId(), topic));

        List<Message> noticeList = messageService.findNotices(SecurityUtil.getUserId(), topic, page.getOffset(), page.getLimit());

        // 需要处理未读状态的私信id
        List<Integer> unreadNoticeIds = new ArrayList<>();

        List<Map<String, Object>> noticeVoList = noticeList.stream().map(notice -> {
            // 筛选未读消息
            if (notice.getToId().equals(SecurityUtil.getUserId()) && notice.getStatus().equals((byte) 0)) {
                unreadNoticeIds.add(notice.getId());
            }
            Map<String, Object> noticeVo = new HashMap<>();
            // 通知
            noticeVo.put("notice", notice);
            // 内容
            String content = HtmlUtils.htmlUnescape(notice.getContent());
            Map<String, Object> data =  JSONObject.parseObject(content, Map.class);

            User user = userService.getUserById((Integer) data.get("userId"));
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user, userVo);
            noticeVo.put("user", userVo);
            noticeVo.put("entityType", data.get("entityType"));
            noticeVo.put("entityId", data.get("entityId"));
            if (data.containsKey("postId")) {
                noticeVo.put("postId", data.get("postId"));
            }
            // 通知作者
            User fromUser = userService.getUserById(notice.getFromId());
            UserVo fromUserVo = new UserVo();
            BeanUtils.copyProperties(fromUser, fromUserVo);
            noticeVo.put("fromUser", fromUserVo);
            return noticeVo;
        }).collect(Collectors.toList());

        // 设置已读
        if (!unreadNoticeIds.isEmpty()) {
            messageService.readMessages(unreadNoticeIds);
        }

        return Result.success(noticeVoList);
    }

    @GetMapping("detail/{topic}/count")
    public Result<Integer> getNotices(@PathVariable("topic") String topic) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Result.success(messageService.findNoticeCount(loginUser.getUser().getId(), topic));
    }
}
