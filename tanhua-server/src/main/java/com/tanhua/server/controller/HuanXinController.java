package com.tanhua.server.controller;

import com.tanha.commons.vo.HuanXinUser;
import com.tanhua.server.interceptor.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/7/9 --星期五  下午 06:00
 **/


/**
 * 即使聊天
 */

@RestController
@RequestMapping("/huanxin")
@Slf4j
public class HuanXinController {

    /**
     * 获取当前登陆的用户名与密码，用于环信的登陆
     * @return
     */
    @GetMapping("/user")
    public ResponseEntity login(){

        HuanXinUser user = new HuanXinUser(UserHolder.getUserId().toString(), "123456",String.format("今晚打老虎_%d",100));
        log.debug("环信已经登录。。。。。");
        return ResponseEntity.ok(user);
    }



}
