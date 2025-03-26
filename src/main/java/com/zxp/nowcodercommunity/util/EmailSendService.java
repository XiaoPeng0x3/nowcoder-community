package com.zxp.nowcodercommunity.util;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailSendService {

    private static final Logger log = LoggerFactory.getLogger(EmailSendService.class);
    // 发送的客户端
    @Autowired
    private JavaMailSender mailSender;

    // 发送的邮件信息
    @Value("${spring.mail.from}")
    private String from;

    /**
     *
     * @param to
     * @param subject
     * @param content
     */
    public void sendSimpleMail(String to, String subject, String content) {
        // 创建发送信息
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        try {
            mimeMessageHelper.setFrom(from);
            log.info("from: " + from);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(content, true); // true表示支持html的内容
            mailSender.send(mimeMessage);
            log.info("发送成功");
        } catch (Exception e) {
            log.error("发送失败{}", e.getMessage());
        }

    }
}

