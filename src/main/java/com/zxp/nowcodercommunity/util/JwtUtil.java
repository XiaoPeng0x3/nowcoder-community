package com.zxp.nowcodercommunity.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zxp.nowcodercommunity.security.model.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.zxp.nowcodercommunity.constant.LoginConstant.LOGIN_USER_KEY;
import static com.zxp.nowcodercommunity.constant.LoginConstant.TOKEN_PREFIX;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);
    /**
     * 设置header
     */
    @Value("${jwt.header}")
    private String header;

    /**
     * 签名密钥
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * 过期时间
     */
    @Value("${jwt.expireTime}")
    private Integer expireTime;

    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    private static final long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;

    private final RedisCache redisCache;

    public JwtUtil(RedisCache redisCache) {
        this.redisCache = redisCache;
    }


    public LoginUser getLoginUser(HttpServletRequest request) {
         // 从HTTP请求中获取token
         String token = getToken(request);
         if (StringUtils.isNotEmpty(token)) {
             try {
                 Claims claims = parseToken(token);
                 // 获取UUID
                 String uuid = (String) claims.get(LOGIN_USER_KEY);
                 // 获取 Redis 缓存中的 key
                 String userKey = RedisKeyUtil.getTokenKey(uuid);
                 LoginUser userDebug = redisCache.getCacheObject(userKey);
                 log.info("user debug: {}", userDebug);
                 return userDebug;
             } catch (Exception e) {
            log.error("获取登录用户失败", e);
        }
         }
         // 对接口进行判断
         return null;

     }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(header);
        if (StringUtils.isNotEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
            token = token.replace(TOKEN_PREFIX, "");
        }
        return token;
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String createToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .serializeToJsonWith(new JacksonSerializer<>())
                .compact();
    }

    public String createToken(LoginUser loginUser) {
        // 生成uuid
        String uuid = CommunityUtil.generateUUID();
        loginUser.setToken(uuid);
        // 设置并缓存
        refreshToken(loginUser);
        Map<String, Object> claims = new HashMap<>(8);
        claims.put(LOGIN_USER_KEY, uuid);
        return createToken(claims);
    }

    public void refreshToken(LoginUser loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = RedisKeyUtil.getTokenKey(loginUser.getToken());
        redisCache.setCacheObject(userKey, loginUser, expireTime, TimeUnit.MINUTES);
    }

    public void verifyToken(LoginUser loginUser) {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TEN) {
            refreshToken(loginUser);
        }
    }

    public void removeToken(String token) {
        if (StringUtils.isNotEmpty(token)) {
            String userKey = RedisKeyUtil.getTokenKey(token);
            redisCache.deleteObject(userKey);
        }
    }
}