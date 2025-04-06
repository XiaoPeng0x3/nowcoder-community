package com.zxp.nowcodercommunity.security.util;

import com.zxp.nowcodercommunity.security.model.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class SecurityUtil {

    public static LoginUser getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser)) {
            return null;
        }
        return (LoginUser) authentication.getPrincipal();
    }

    public static Integer getUserId() {
        LoginUser user = getLoginUser();
        return user != null ? user.getUser().getId() : null;
    }
}


