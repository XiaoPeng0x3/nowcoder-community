package com.zxp.nowcodercommunity.constant;

public class RegisterConstants {

    /**
     * 注册时传递过来的参数为空
     */
    public static final String USER_NULL = "User参数为空";

    /**
     * User的username为空
     */
    public static final String USERNAME_NULL = "Username为空";

    /**
     * User的email为空
     */
    public static final String USER_EMAIL_NULL = "Useremail为空！";

    /**
     * User已经存在
     */
    public static final String USER_EXIST = "User已经存在";

    /**
     * User的password为空
     */
    public static final String USER_PASSWORD_NULL = "User密码为空";

    /**
     * User的用户名重复
     */
    public static final String USER_NAME_EXIST = "User的用户名已经存在！";

    /**
     * User的Email重复
     */
    public static final String USER_EMAIL_EXIST = "User的email已经被注册！";

    /**
     * 普通用户
     */
    public static final int USER_COMMON = 0;

    /**
     * 管理员
     */
    public static final int USER_ADMIN = 1;

    /**
     * 版主
     */
    public static final int USER_HOST = 2;

    /**
     * 设置用户的未激活状态
     */
    public static final int USER_DEACTIVATE = 0;

    /**
     * 设置用户的激活状态
     */
    public static final int USER_ACTIVATE = 1;

    /**
     * 注册成功
     */
    public static final String SUCCESS = "注册成功，欢迎你！";
}
