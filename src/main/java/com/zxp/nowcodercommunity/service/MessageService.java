package com.zxp.nowcodercommunity.service;

import com.zxp.nowcodercommunity.mapper.MessageMapper;
import com.zxp.nowcodercommunity.pojo.Message;
import org.springframework.stereotype.Service;

import java.util.List;

public interface MessageService {
    /**
     * 查询会话数量
     * @param userId
     * @return
     */
    Integer selectConversationCount(Integer userId);

    /**
     * 对话分页显示
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Message> findConversations(Integer userId, int offset, int limit);

    /**
     * 私信数
     * @param conversationId
     * @return
     */
    Integer findLetterCount(String conversationId);

    /**
     * 找到未读消息
     * @param userId
     * @param conversationId
     * @return
     */
    Integer findLetterUnreadCount(Integer userId, String conversationId);

    /**
     * 向数据库添加一则消息
     * @param message
     * @return
     */
    Integer addMessage(Message message);

    /**
     * 根据userId查询最新消息
     * @param userId
     * @param topic
     * @return
     */
    Message findLatestNotice(Integer userId, String topic);

    /**
     * 通知数
     * @param userId
     * @param topic
     * @return
     */
    Integer findNoticeCount(Integer userId, String topic);

    /**
     * 未读的通知数
     * @param userId
     * @param topic
     * @return
     */
    Integer findNoticeUnreadCount(Integer userId, String topic);

    /**
     * 分页查找notice
     * @param userId
     * @param topic
     * @param offset
     * @param limit
     * @return
     */
    List<Message> findNotices(int userId, String topic, int offset, int limit);

    int readMessages(List<Integer> unreadNoticeIds);
}
