package com.tanha.commons;

import com.tanha.commons.properties.FaceProperties;
import com.tanha.commons.properties.HuanXinProperties;
import com.tanha.commons.properties.OssProperties;
import com.tanha.commons.properties.SmsProperties;
import com.tanha.commons.templates.FaceTemplate;
import com.tanha.commons.templates.HuanXinTemplate;
import com.tanha.commons.templates.OssTemplate;
import com.tanha.commons.templates.SmsTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 自动配置类
 */
@Configuration
@EnableConfigurationProperties({SmsProperties.class, OssProperties.class, FaceProperties.class, HuanXinProperties.class})
public class CommonsAutoConfiguration {

    @Bean
    public SmsTemplate smsTemplate(SmsProperties smsProperties){
        SmsTemplate smsTemplate = new SmsTemplate(smsProperties);
        smsTemplate.init();
        return smsTemplate;
    }
    @Bean
    public OssTemplate ossTemplate(OssProperties ossProperties){
        return new OssTemplate(ossProperties);
    }

    @Bean
    public FaceTemplate faceTemplate(FaceProperties faceProperties){
        return new FaceTemplate(faceProperties);
    }

    @Bean
    public HuanXinTemplate huanXinTemplate(HuanXinProperties huanXinProperties){
      return    new HuanXinTemplate(huanXinProperties);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder.build();
    }
}
