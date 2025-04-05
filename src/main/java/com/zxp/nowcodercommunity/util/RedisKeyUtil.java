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

    public static String getTokenKey(String uuid) {
        return PREFIX_LOGIN_TOKEN + uuid;
    }
}
