package com.zxp.nowcodercommunity.service;

public interface LikeService {

    /**
     * 点赞功能
     */
    void like(Integer userId, Integer entityType, Integer entityId, Integer entityUserId);

    /**
     * 点赞数量
     */
    Long findEntityLikeCount(Integer entityType, Integer entityId);

    /**
     * 某人对实体的点赞状态
     */
    int findEntityLikeStatus( Integer userId, Integer entityType, Integer entityId);

    /**
     * 某个用户获取的点赞数量
     */

    Long findUserLikeCount(Integer userId);
}
