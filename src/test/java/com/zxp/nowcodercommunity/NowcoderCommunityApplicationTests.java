package com.zxp.nowcodercommunity;

import com.zxp.nowcodercommunity.util.EmailSendService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NowcoderCommunityApplicationTests {

    @Autowired
    EmailSendService emailSendService;

    @Test
    void contextLoads() {
        System.out.println("Hello");
    }

    @Test
    void emailSendTest() {
        emailSendService.sendSimpleMail("3436854592@qq.com", "A Test Email From SpringBoot Auto Send", "Hello,World!");
    }

}
