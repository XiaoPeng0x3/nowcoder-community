package com.zxp.nowcodercommunity.util.check;

import com.zxp.nowcodercommunity.pojo.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UserCheck {
    private static final Logger log = LoggerFactory.getLogger(UserCheck.class);


    public static boolean checkUser(User user) {
        return user != null;
    }

    public static boolean checkPassword(User user) {
        String password = user.getPassword();
        if (StringUtils.isBlank(password)) {
            log.info("password is null");
            return false;
        }
        return true;
    }

    public static boolean checkEmail(User user) {
        String email = user.getEmail();
        if (StringUtils.isBlank(email)) {
            log.info("email is null");
            return false;
        }
        return true;
    }
}
