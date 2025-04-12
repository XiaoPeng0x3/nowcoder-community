package com.zxp.nowcodercommunity.dto;

import lombok.Data;

public class FollowDto {
    private int entityType;
    private int entityId;

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

    public FollowDto(int entityType, int entityId) {
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public FollowDto() {
    }
}
