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

    Integer findLetterUnreadCount(Integer userId, String conversationId);
}
