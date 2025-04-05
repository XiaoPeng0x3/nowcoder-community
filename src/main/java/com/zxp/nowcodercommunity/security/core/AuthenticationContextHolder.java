package com.zxp.nowcodercommunity.security.core;

import org.springframework.security.core.Authentication;

public class AuthenticationContextHolder {
    private static final ThreadLocal<Authentication> AUTHENTICATION_CONTEXT = new ThreadLocal<Authentication>();

    public static Authentication getAuthentication() {
        return AUTHENTICATION_CONTEXT.get();
    }
    public static void setAuthentication(Authentication authentication) {
        AUTHENTICATION_CONTEXT.set(authentication);
    }
    public static void clearAuthentication() {
        AUTHENTICATION_CONTEXT.remove();
    }
}
