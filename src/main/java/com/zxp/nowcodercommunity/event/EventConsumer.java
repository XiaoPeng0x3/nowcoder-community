package com.zxp.nowcodercommunity.event;

import com.alibaba.fastjson2.JSONObject;
import com.zxp.nowcodercommunity.annotation.AutoCreateTime;
import com.zxp.nowcodercommunity.pojo.DiscussPost;
import com.zxp.nowcodercommunity.pojo.Event;
import com.zxp.nowcodercommunity.pojo.Message;
import com.zxp.nowcodercommunity.security.util.SecurityUtil;
import com.zxp.nowcodercommunity.service.DiscussPostService;
import com.zxp.nowcodercommunity.service.MessageService;
import com.zxp.nowcodercommunity.util.FileUpload;
import com.zxp.nowcodercommunity.util.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.zxp.nowcodercommunity.constant.LoginConstant.*;

@Component
public class EventConsumer {

    private static final Logger log = LoggerFactory.getLogger(EventConsumer.class);
    private final MessageService messageService;

    private final DiscussPostService discussPostService;


    private final FileUpload fileUploadUtil;

    private final RedisCache redisCache;

    private final ThreadPoolTaskScheduler taskScheduler;

    public EventConsumer(MessageService messageService, DiscussPostService discussPostService, FileUpload fileUploadUtil, RedisCache redisCache, ThreadPoolTaskScheduler taskScheduler) {
        this.messageService = messageService;
        this.discussPostService = discussPostService;
        this.fileUploadUtil = fileUploadUtil;
        this.redisCache = redisCache;
        this.taskScheduler = taskScheduler;
    }

    /**
     * 处理消息
     * @param record
     */
    @AutoCreateTime // 自动抽取时间字段
    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord<String, Object> record) {
        if (record == null || record.value() == null) {
            log.error("消息的内容为空！");
            return;
        }

        // 将消息的内容转换为JSON字符串
        // 发送消息的时候把消息转换为了JSON的字符串
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            log.error("消息格式错误！");
            return;
        }

        // 发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID); // 系统后台虚拟用户
        message.setToId(event.getEntityUserId()); // 真是接受到的消息
        message.setConversationId(event.getTopic()); // 对应的主题
        message.setStatus((byte) 0);
        message.setCreateTime(LocalDateTime.now());

        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        if (!event.getData().isEmpty()) {
            content.putAll(event.getData()); // 全部放到content里面
        }
        // 内容是拼接出来的一句话
        message.setContent(JSONObject.toJSONString(content));
        // 把这条消息添加到数据库里面
        messageService.addMessage(message);
    }

    /**
     * 消费发帖事件
     * @param record 消费记录
     */
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord<String, Object> record) {
        if (record == null || record.value() == null) {
            log.error("消息的内容为空！");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            log.error("消息格式错误！");
            return;
        }

        // DiscussPost discussPost = discussPostService.findPostById(event.getEntityId());
    }

    /**
     * 消费发帖事件
     * @param record 消费记录
     */
    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDeleteMessage(ConsumerRecord<String, Object> record) {
        if (record == null || record.value() == null) {
            log.error("消息的内容为空！");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            log.error("消息格式错误！");
            return;
        }
    }
}
