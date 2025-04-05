package com.zxp.nowcodercommunity;

import com.zxp.nowcodercommunity.util.EmailSendService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;

@SpringBootTest
class NowcoderCommunityApplicationTests {

    @Autowired
    EmailSendService emailSendService;

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        System.out.println("Hello");
    }

    @Test
    void emailSendTest() {
        emailSendService.sendSimpleMail("3436854592@qq.com", "A Test Email From SpringBoot Auto Send", "Hello,World!");
    }

    /**
     * 字符串测试
     */
    @Test
    void redisStringTest() {
        // 首先初始化redisOps对象
        ValueOperations opsForValue = redisTemplate.opsForValue();
        opsForValue.set("hello", "world");
    }

    /**
     *  哈希表测试
     */
    @Test
    void redisHashTest() {
        HashOperations hashOperations = redisTemplate.opsForHash();
        hashOperations.put("zxp:user", "name", "lisi");
        hashOperations.put("zxp:user", "age", 18);
    }

    /**
     *  List测试
     */
    @Test
    void redisListTest() {
        ListOperations listOperations = redisTemplate.opsForList();
        listOperations.leftPush("zxp:user2", 1);
        listOperations.leftPush("zxp:user2", 2);
    }

    /**
     * set
     */
    @Test
    void redisSetTest() {
        SetOperations setOperations = redisTemplate.opsForSet();
        setOperations.add("zxp:user3", 1);
        setOperations.add("zxp:user3", 2);
    }

    /**
     * 有序zset
     */
    @Test
    void redisZSetTest() {
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add("zxp:user4", "lisi", 20);
        zSetOperations.add("zxp:user4", "wangwu", 10);
        zSetOperations.add("zxp:user4", "zhangsan", 70);
    }

}
