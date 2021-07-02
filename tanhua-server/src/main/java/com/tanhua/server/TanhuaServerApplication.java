package com.tanhua.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/6/24 --星期四  下午 05:20
 **/
@SpringBootApplication(exclude = MongoAutoConfiguration.class)
public class TanhuaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TanhuaServerApplication.class,args);
    }
}
