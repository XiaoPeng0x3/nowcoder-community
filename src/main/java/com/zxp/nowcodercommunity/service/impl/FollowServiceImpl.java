package com.zxp.nowcodercommunity.service.impl;

import com.zxp.nowcodercommunity.pojo.User;
import com.zxp.nowcodercommunity.security.core.AuthenticationContextHolder;
import com.zxp.nowcodercommunity.security.model.LoginUser;
import com.zxp.nowcodercommunity.service.FollowService;
import com.zxp.nowcodercommunity.service.UserService;
import com.zxp.nowcodercommunity.util.RedisKeyUtil;
import com.zxp.nowcodercommunity.vo.UserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.zxp.nowcodercommunity.constant.LoginConstant.ENTITY_TYPE_USER;

@Service
public class FollowServiceImpl implements FollowService {

    private static final Logger log = LoggerFactory.getLogger(FollowServiceImpl.class);
    private final RedisTemplate redisTemplate;
    private final UserService userService;
    public FollowServiceImpl(RedisTemplate redisTemplate, UserService userService) {
        this.redisTemplate = redisTemplate;
        this.userService = userService;
    }

    /**
     * 实现关注功能
     * @param userId
     * @param entityId
     * @param entityType
     */
    @Override
    public void follow(Integer userId, Integer entityId, Integer entityType) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 获取粉丝key
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId); // 某个实体的被关注数
                // 获取关注key
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType); // 某个用户关注了哪些实体

                // 开启事务
                operations.multi();

                // 执行插入操作
                operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis()); // 粉丝列表
                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis()); // 关注列表

                // 事务提交
                return operations.exec();
            }
        });
    }

    @Override
    public void unfollow(Integer userId, Integer entityId, Integer entityType) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 获取粉丝key
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                // 获取关注key
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);

                // 开启事务
                operations.multi();

                // 删除
                operations.opsForZSet().remove(followerKey, userId); // 粉丝列表
                operations.opsForZSet().remove(followeeKey, entityId); // 关注列表


                // 事务提交
                return operations.exec();
            }
        });
    }

    /**
     * 找到userId的关注数
     *
     * @param userId
     * @param entityType
     * @return
     */
    @Override
    public Integer findFolloweeCount(Integer userId, Integer entityType) {
        // 得到key
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return Math.toIntExact(redisTemplate.opsForZSet().zCard(followeeKey) == null ? 0 : redisTemplate.opsForZSet().zCard(followeeKey));
    }


    /**
     * 找到粉丝数目
     * @param entityType
     * @param entityId
     * @return
     */
    @Override
    public Integer findFollowerCount(Integer entityType, Integer entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return Math.toIntExact(redisTemplate.opsForZSet().zCard(followerKey) == null ? 0 : redisTemplate.opsForZSet().zCard(followerKey));
    }

    /**
     * 查找userId关注列表
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    @Override
    public List<Map<String, Object>> findFollowees(Integer userId, Integer offset, Integer limit) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, (int) ENTITY_TYPE_USER);
        // 从redis里面查找
        Set<Object> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1); // 得到IDs
        if (targetIds == null) {
            return null;
        }

        // 查找当前登录用户是否也关注了某个user,可以用来‘显示互相关注’
        LoginUser loginUser = (LoginUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // 获取key
        String loginUserKey = RedisKeyUtil.getFolloweeKey(loginUser.getUser().getId(), (int) ENTITY_TYPE_USER);

        // 构造map
        List<Map<String, Object>> data = targetIds.stream().map(id -> {
            Map<String, Object> map = new HashMap<>();
            // 查找User
            User userById = userService.getUserById((Integer) id);
            UserVo userVo = new UserVo();
            if (userById != null) {
                BeanUtils.copyProperties(userById, userVo);
            }
            // 把userVo放进去
            map.put("user", userVo);

            // 按照 “关注时间排序”
            Double score = redisTemplate.opsForZSet().score(followeeKey, id);
            if (score != null) {
                map.put("followTime", Instant.ofEpochMilli(score.longValue()).atZone(ZoneId.systemDefault())
                        .toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }

            // 是否互相关注
            if (!(loginUser.getUser().getId() == (int)id)) {
                map.put("followed", redisTemplate.opsForZSet().score(loginUserKey, id) != null);
            }
            return map;
        }).collect(Collectors.toList());
        log.info("findFolloweeCount: {}", data);
        return data;
    }

    @Override
    public List<Map<String, Object>> findFollowers(int userId, int offset, int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(userId, (int) ENTITY_TYPE_USER);
        // 从redis里面查找
        Set<Object> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1); // 得到IDs
        if (targetIds == null) {
            return null;
        }

        // 查找当前登录用户是否也关注了某个user,可以用来‘显示互相关注’
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("principal: {}", principal);
        LoginUser loginUser = (LoginUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // 获取key
        String loginUserKey = RedisKeyUtil.getFolloweeKey(loginUser.getUser().getId(), (int) ENTITY_TYPE_USER);

        // 构造map
        List<Map<String, Object>> data = targetIds.stream().map(id -> {
            Map<String, Object> map = new HashMap<>();
            // 查找User
            User userById = userService.getUserById((Integer) id);
            UserVo userVo = new UserVo();
            if (userById != null) {
                BeanUtils.copyProperties(userById, userVo);
            }
            // 把userVo放进去
            map.put("user", userVo);

            // 按照 “关注时间排序”
            Double score = redisTemplate.opsForZSet().score(followerKey, id);
            if (score != null) {
                map.put("followTime", Instant.ofEpochMilli(score.longValue()).atZone(ZoneId.systemDefault())
                        .toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }

            // 是否互相关注
            if (!(loginUser.getUser().getId() == (int)id)) {
                map.put("followed", redisTemplate.opsForZSet().score(loginUserKey, id) != null);
            }
            return map;
        }).toList();
        log.info("data", data);
        return data;
    }

    /**
     * 是否关注
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    @Override
    public Boolean followed(Integer userId, int entityType, int entityId) {
        // 得到userId的关注列表
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        // 判断entityId是否在用户的关注列表里面
        log.info("分数={}" ,redisTemplate.opsForZSet().score(followeeKey, entityId));
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }
}
