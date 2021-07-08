package com.tanhua.server.interceptor;

import com.tanhua.domain.db.User;
import org.springframework.stereotype.Component;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/6/29 --星期二  下午 04:21
 **/

/**
 * 保存用户信息到Threadlocal中
 */
@Component
public class UserHolder {

    private static final  ThreadLocal<User> thread = new ThreadLocal<>();


    public static User getUser(){
        return thread.get();
    }

    public static void setUser(User user){
        thread.set(user);
    }

    public static Long getUserId(){
        return getUser().getId();
    }


}
