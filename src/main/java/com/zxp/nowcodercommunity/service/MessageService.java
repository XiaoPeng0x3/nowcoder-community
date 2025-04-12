package com.zxp.nowcodercommunity.service;

import com.zxp.nowcodercommunity.mapper.MessageMapper;
import com.zxp.nowcodercommunity.pojo.Message;
import org.springframework.stereotype.Service;

import java.util.List;

public interface MessageService {

    Integer selectConversationCount(Integer userId);

    List<Message> findConversations(Integer userId, int offset, int limit);

    Integer findLetterCount(String conversationId);

    Integer findLetterUnreadCount(Integer userId, String conversationId);
}
