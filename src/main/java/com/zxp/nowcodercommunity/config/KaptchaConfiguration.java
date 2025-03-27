package com.zxp.nowcodercommunity.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfiguration {

    @Bean
    public Producer kaptchaProducer() {
        // 参数配置
        Properties props = new Properties();
        props.setProperty("kaptcha.image.width", "100"); // 设置图像宽度
        props.setProperty("kaptcha.image.height", "40"); // 设置图像高度
        props.setProperty("kaptcha.textproducer.font.size", "32"); // 字体大小
        props.setProperty("kaptcha.textproducer.font.color", "0,0,0"); // 字体的颜色,RGB
        props.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"); // 生成的字符集
        props.setProperty("kaptcha.textproducer.char.length", "4"); // 字符长度
        props.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise"); // 随机噪声
        // 生成kaptcha对象
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha(); // 验证码对象
        Config config = new Config(props); // 写入配置
        defaultKaptcha.setConfig(config); // 设置配置
        return defaultKaptcha;
    }
}
