package com.zxp.nowcodercommunity.service.impl;

import com.zxp.nowcodercommunity.service.LikeService;
import com.zxp.nowcodercommunity.util.RedisCache;
import com.zxp.nowcodercommunity.util.RedisKeyUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {
    // 注入redisTemplate
    private final RedisCache redisCache;
    private final RedisTemplate redisTemplate;

    public LikeServiceImpl(RedisCache redisCache, RedisTemplate redisTemplate) {
        this.redisCache = redisCache;
        this.redisTemplate = redisTemplate;
    }

    /**
     *
     * @param userId
     * @param entityType
     * @param entityId
     */
    @Override
    public void like(Integer userId, Integer entityType, Integer entityId, Integer entityUserId) {
//        // 将数据存储在redis里面
//
//        // 1. 生成key
//        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
//
//        // 2. 存入redis里面
//        Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
//        if (isMember) { // 存在说明已经点过赞了，再次点赞就是取消点赞
//            redisTemplate.opsForSet().remove(entityLikeKey, userId);
//        } else { // 否则就添加到集合里面
//            redisTemplate.opsForSet().add(entityLikeKey, userId);
//        }

        // 点赞操作开启事务
        redisTemplate.execute(new SessionCallback() {

            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 得到entityLikeKey
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                // 得到entityUserId
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId); // 被点赞的那个userId

                // 判断userId这个人是否点过赞
                /**
                 *  注意不要再redis开启事务的时候进行查询
                 */
                Boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);

                // 开启事务
                operations.multi();


                if (Boolean.TRUE.equals(isMember)) {
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey); // 被点赞那个人的赞数-1
                } else {
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }

                // 执行事务
                return operations.exec();
            }
        });
    }

    /**
     * 统计数据数量
     * @param entityType
     * @param entityId
     * @return
     */
    @Override
    public Long findEntityLikeCount(Integer entityType, Integer entityId) {
        // 获取key
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        // 查询这个key对应集合的数量
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    /**
     * 查看某个userId用户是否点赞
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    @Override
    public int findEntityLikeStatus(Integer userId, Integer entityType, Integer entityId) {
        // 获取key
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        // 查看userId是否是集合的元素
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    @Override
    public Long findUserLikeCount(Integer userId) {
        // 从redis里面查询
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        return redisTemplate.opsForValue().size(userLikeKey);
    }


}
