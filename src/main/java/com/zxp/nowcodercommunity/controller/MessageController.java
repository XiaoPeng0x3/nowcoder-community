package com.zxp.nowcodercommunity.controller;

import com.zxp.nowcodercommunity.pojo.Message;
import com.zxp.nowcodercommunity.pojo.Page;
import com.zxp.nowcodercommunity.pojo.User;
import com.zxp.nowcodercommunity.result.Result;
import com.zxp.nowcodercommunity.security.util.SecurityUtil;
import com.zxp.nowcodercommunity.service.MessageService;
import com.zxp.nowcodercommunity.service.UserService;
import com.zxp.nowcodercommunity.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  管理与消息有关的controller
 */

@RestController
@RequestMapping("letter")
public class MessageController {

    // 注入service层对象
    private final MessageService messageService;
    private final UserService userService;
    public MessageController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @GetMapping("list")
    public Result<List<Map<String, Object>>> getLetterList(@RequestParam("current") int current,
                                                           @RequestParam("limit") int limit) {

        // 获取与分页有关的内容
        Page page = new Page();
        page.setCurrent(current);
        page.setLimit(limit);

        // 获取userId下的所有评论
        page.setRows(messageService.selectConversationCount(SecurityUtil.getUserId()));

        // 展示消息列表
        List<Message> conversations = messageService.findConversations(SecurityUtil.getUserId(), page.getOffset(), page.getLimit());

        // 然后使用mapper进行初始化
        /**
         *  需要填充三个字段
         *  conversation 查询的对话
         *  letterCount 对话里面的总条数
         *  unreadCount 未读数目
         */
        Integer userId = SecurityUtil.getUserId();
        List<Map<String, Object>> conversion = conversations.parallelStream().map(message -> {
            Map<String, Object> messageVo = new HashMap<>();
            messageVo.put("conversation", message);
            // 对话里面的条数需要根据sql去进行查询
            messageVo.put("letterCount", messageService.findLetterCount(message.getConversationId()));
            // 找到未读数目
            // 未读消息是别人发送给user的
            messageVo.put("unreadCount", messageService.findLetterUnreadCount(userId, message.getConversationId()));

            // 发送消息的那个人
            Integer targetId = userId.equals(message.getFromId()) ? message.getToId() : message.getFromId();
            // 根据targetId找到User
            User user = userService.getUserById(targetId);
            UserVo userVo = new UserVo();
            if (user != null) {
                BeanUtils.copyProperties(user, userVo);
            }
            messageVo.put("target", userVo);
            return messageVo;
        }).toList();


        return Result.success(conversion);
    }

    /**
     *  根据用户id查询未读消息数量
     */
    @GetMapping("unread/count")
    public Result<Integer> getLetterUnreadCount() {
        Integer letterUnreadCount = messageService.findLetterUnreadCount(SecurityUtil.getUserId(), null);
        return Result.success(letterUnreadCount);
    }


}
