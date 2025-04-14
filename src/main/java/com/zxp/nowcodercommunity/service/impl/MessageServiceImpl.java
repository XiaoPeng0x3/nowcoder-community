package com.zxp.nowcodercommunity.service.impl;

import com.zxp.nowcodercommunity.mapper.MessageMapper;
import com.zxp.nowcodercommunity.pojo.Message;
import com.zxp.nowcodercommunity.service.MessageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    //mapper层对象
    private final MessageMapper messageMapper;

    public MessageServiceImpl(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    /**
     * 根据用户id来查看最新的消息数
     * @param userId
     */
    @Override
    public Integer selectConversationCount(Integer userId) {
        return messageMapper.selectConversationCount(userId);
    }

    /**
     *  分页显示用户所有的消息
     */
    @Override
    public List<Message> findConversations(Integer userId, int offset, int limit) {
        // 调用mapper层的接口
        return messageMapper.findConversations(userId, offset, limit);
    }

    @Override
    public Integer findLetterCount(String conversationId) {
        return messageMapper.findLetterCount(conversationId);
    }

    @Override
    public Integer findLetterUnreadCount(Integer userId, String conversationId) {
        return messageMapper.findLetterUnreadCount(userId, conversationId);
    }

    @Override
    public Integer addMessage(Message message) {
        return messageMapper.addMessage(message);
    }

    @Override
    public Message findLatestNotice(Integer userId, String topic) {
        return messageMapper.selectLatestNotice(userId, topic);
    }

    @Override
    public Integer findNoticeCount(Integer userId, String topic) {
        return messageMapper.selectNoticeCount(userId, topic);
    }

    @Override
    public Integer findNoticeUnreadCount(Integer userId, String topic) {
        return messageMapper.selectNoticeUnreadCount(userId, topic);
    }

    @Override
    public List<Message> findNotices(int userId, String topic, int offset, int limit) {
        return messageMapper.findNotices(userId, topic, offset, limit);
    }

    @Override
    public int readMessages(List<Integer> unreadNoticeIds) {
        return messageMapper.readMessages(unreadNoticeIds, (byte)1);
    }
}
