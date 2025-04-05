package com.zxp.nowcodercommunity.constant;

/**
 * 登录时的一些常量值
 */
public final class LoginConstant {
    /**
     * 权限：普通用户
     */
    public static final String AUTHORITY_USER = "user";

    /**
     * 权限：管理员
     */
    public static final String AUTHORITY_ADMIN = "admin";

    /**
     * 权限：版主
     */
    public static final String AUTHORITY_MODERATOR = "moderator";

    /**
     * 激活成功
     */
    public static final int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    public static final int ACTIVATION_REPEAT = 1;

    /**
     * 激活成功
     */
    public static final int ACTIVATION_FAILURE = 2;

    /**
     * 默认的登录凭证超时时间
     */
    public static final int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 记住我状态的登录凭证超时时间
     */
    public static final int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 7;

    /**
     * 实体类型：帖子
     */
    public static final byte ENTITY_TYPE_POST = 1;

    /**
     * 实体类型：评论
     */
    public static final byte ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型：用户
     */
    public static final byte ENTITY_TYPE_USER = 3;

    /**
     * 主题：评论
     */
    public static final String TOPIC_COMMENT = "comment";

    /**
     * 主题：点赞
     */
    public static final String TOPIC_LIKE = "like";

    /**
     * 主题：关注
     */
    public static final String TOPIC_FOLLOW = "follow";

    /**
     * 主题：发帖
     */
    public static final String TOPIC_PUBLISH = "publish";


    /**
     * 主题：删帖
     */
    public static final String TOPIC_DELETE = "delete";

    /**
     * 主题：分享
     */
    public static final String TOPIC_SHARE = "share";

    /**
     * 系统用户ID
     */
    public static final int SYSTEM_USER_ID = 1;


    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 令牌前缀
     */
    public static final String LOGIN_USER_KEY = "login_jwt_key";

    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "jwt_tokens:";
}
