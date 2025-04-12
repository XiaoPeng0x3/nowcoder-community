package com.zxp.nowcodercommunity.util;

import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *  这个类用来存储redis的不同类型的数据
 */
@Component
public class RedisCache {
    // 构造函数的方式进行注入
    private RedisTemplate redisTemplate;
    // 构造函数
    public RedisCache (RedisTemplate redisTemplate) {
        // 所有操作都是通过这个redisTemplate的工具实现的
        this.redisTemplate = redisTemplate;
    }

    /**
     *  缓存基本的数据类型，例如Integer, String这些
     * @param key
     * @param value
     * @param <T>
     */
    public <T> void setCacheObject (final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的数据类型，例如Integer, String这些， 同时也给这些数据设置过期时间
     * @param key
     * @param value
     * @param timeout
     * @param timeUnit
     * @param <T>
     */
    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit){
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 给指定的key设置过期时间
     * @param key
     * @param timeout
     * @param timeUnit
     * @return
     */
    public boolean expire (final String key, final long timeout, final TimeUnit timeUnit){
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, timeUnit));
    }

    /**
     * 给指定的key设置过期时间
     * @param key
     * @param timeout
     * @return
     */
    public boolean setExpire (final String key, final long timeout){
        return expire(key, timeout, TimeUnit.SECONDS); // 单位为秒
    }

    /**
     *  得到redis中的数据
     * @param key
     * @return
     * @param <T>
     */
    public <T> T getCacheObject (final String key) {
        ValueOperations<String, T> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    /**
     * 根据key来删除指定对象
     * @param key
     * @return
     */
    public boolean delCacheObject (final String key){
        return redisTemplate.delete(key);
    }

    /**
     * 缓存List的数据
     * @param key
     * @param list
     * @return
     * @param <T>
     */
    public <T> boolean setListCacheObject (final String key, final List<T> list){
        Long count = redisTemplate.opsForList().rightPush(key, list);
        return count != null && count > 0;
    }

    /**
     * 得到List的所有数据
     * @param key
     * @return
     * @param <T>
     */
    public <T> List<T> getListCacheObject (final String key){
        return (List<T>) redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public boolean deleteObject(final String key)
    {
        return redisTemplate.delete(key);
    }

    /**
     * 缓存Set元素
     *
     * @param key 缓存键值
     * @param value 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> BoundSetOperations<String, T> setCacheSet(final String key, final T value)
    {
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        setOperation.add(value);
        return setOperation;
    }
}
