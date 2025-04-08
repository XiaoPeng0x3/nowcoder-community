package com.zxp.nowcodercommunity.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 *  评论的实体类
 */
public class Comment {

    private Integer id;
    // 发布评论的人
    private Integer userId;
    // 评论的类型，包含回帖和底下用户之间的回复
    private Byte entityType;
    // 帖子的id
    private Integer entityId;
    // 被回复的userid
    private Integer targetId;
    private String content;
    private Byte status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Byte getEntityType() {
        return entityType;
    }

    public void setEntityType(Byte entityType) {
        this.entityType = entityType;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Comment(Integer id, Integer userId, Byte entityType, Integer entityId, Integer targetId, String content, Byte status, LocalDateTime createTime) {
        this.id = id;
        this.userId = userId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.targetId = targetId;
        this.content = content;
        this.status = status;
        this.createTime = createTime;
    }

    public Comment() {
    }
}
