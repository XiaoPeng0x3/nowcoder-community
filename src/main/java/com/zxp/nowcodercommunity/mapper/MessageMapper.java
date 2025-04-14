package com.zxp.nowcodercommunity.mapper;

import com.zxp.nowcodercommunity.pojo.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    /**
     *  根据用户id来查询最新的消息
     *  包括别人发送给用户的或者用户发送给别人的
     */
    Integer selectConversationCount(Integer userId);


    /**
     * user的消息的分页展示
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Message> findConversations(Integer userId, int offset, int limit);

    /**
     * 根据conversationId找到消息总数
     * @param conversationId
     * @return
     */
    Integer findLetterCount(String conversationId);

    /**
     * 根据userId和conversationId来查询未读消息
     * @param userId
     * @param conversationId
     * @return
     */
    Integer findLetterUnreadCount(Integer userId, String conversationId);

    /**
     * 添加一则消息
     * @param message
     * @return
     */
    Integer addMessage(Message message);

    /**
     * 查询某个主题下最新的通知
     */
    Message selectLatestNotice(Integer userId, String topic);

    /**
     * 查询某个主题所包含的通知数量
     */
    Integer selectNoticeCount(Integer userId, String topic);

    /**
     * 查询未读的通知数量
     */
    Integer selectNoticeUnreadCount(Integer userId, String topic);

    List<Message> findNotices(int userId, String topic, int offset, int limit);

    int readMessages(List<Integer> unreadNoticeIds, byte status);
}
