package com.zxp.nowcodercommunity.dto;

public class LikeDto {

    private int entityType;

    private int entityId;

    private int entityUserId;

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public void setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
    }

    public LikeDto(int entityType, int entityId, int entityUserId) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.entityUserId = entityUserId;
    }

    public LikeDto() {
    }
}
