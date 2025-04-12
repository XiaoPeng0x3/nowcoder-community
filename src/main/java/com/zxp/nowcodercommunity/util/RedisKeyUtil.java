package com.zxp.nowcodercommunity.util;

/**
 *  这个类就是在为redis的key来进行字符串的拼接
 */
public final class RedisKeyUtil {
    // 常量值
    private static final String PREFIX_KAPTCHA = "kaptcha:";

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity:";
    private static final String PREFIX_USER_LIKE = "like:user:";
    private static final String PREFIX_FOLLOWEE = "followee:";
    private static final String PREFIX_FOLLOWER = "follower:";
    private static final String PREFIX_TICKET = "ticket:";
    private static final String PREFIX_FORGET_CODE = "forget:";
    private static final String PREFIX_USER = "user:";
    private static final String PREFIX_LOGIN_TOKEN = "token:";
    private static final String PREFIX_LOGIN_USER = "login:user:";
    private static final String PREFIX_UV = "uv:";
    private static final String PREFIX_DAU = "dau:";
    private static final String PREFIX_POST = "post:";

    private static final String PREFIX_SHARE = "share:";

    /**
     * 获取验证码
     */
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + owner;
    }

    /**
     * jwt
     * @param uuid
     * @return
     */
    public static String getTokenKey(String uuid) {
        return PREFIX_LOGIN_TOKEN + uuid;
    }

    /**
     *  点赞功能
     *  当某个用户点赞的时候，对于这个实体(帖子和点赞)，可以将这个key封装起来
     *  其key的形式就是 like:entity:entityType:entityId
     *  这个key存储的value就是一个实体类型的集合
     *  使用集合可以方便的统计帖子的点赞数和点赞用户
     */

    public static String getEntityLikeKey(Integer entityType, Integer entityId) {
        return PREFIX_ENTITY_LIKE + entityType + SPLIT + entityId;
    }

    /**
     * 某个用户的赞
     */
    public static String getUserLikeKey(Integer userId) {
        return PREFIX_USER_LIKE + userId;
    }

    /**
     * 用户的关注，可以是某个人或者某个帖子
     * prefix:userId:entityType -> zet(entityId, now)
     */
    public static String getFolloweeKey(Integer userId, Integer entityType) {
        return PREFIX_FOLLOWEE + userId + SPLIT + entityType;
    }

    /**
     *  用户的粉丝
     *  prefix:entityType:entityId ->zset(userId, now)
     */
    public static String getFollowerKey(Integer entityType, Integer entityId) {
        return PREFIX_FOLLOWER + entityType + SPLIT + entityId;
    }
}
