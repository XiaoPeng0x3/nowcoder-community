package com.zxp.nowcodercommunity.service;

import java.util.List;
import java.util.Map;

public interface FollowService {

    /**
     * 关注功能
     */
    void follow(Integer userId, Integer entityId, Integer entityType);

    // 取消关注
    void unfollow(Integer userId, Integer entityId, Integer entityType);

    // userId用户是否关注
    Boolean followed(Integer userId, int entityType, int entityId);

    // 关注数
    Integer findFolloweeCount(Integer userId, Integer entityType);

    // 粉丝数
    Integer findFollowerCount(Integer entityType, Integer entityId);

    // 查询关注
    List<Map<String, Object>> findFollowees(Integer userId, Integer offset, Integer limit);

    // 查询某用户的粉丝
    List<Map<String, Object>> findFollowers(int userId, int offset, int limit);
}
