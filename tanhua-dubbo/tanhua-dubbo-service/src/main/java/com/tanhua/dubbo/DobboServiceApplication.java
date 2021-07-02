package com.tanhua.dubbo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/6/24 --星期四  下午 02:01
 **/
@SpringBootApplication
@MapperScan("com.tanhua.dubbo.mapper")
public class DobboServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DobboServiceApplication.class,args);
    }
}
