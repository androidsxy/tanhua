package com.tanhua.server.interceptor;

import com.tanhua.domain.db.User;
import com.tanhua.server.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/6/29 --星期二  下午 04:16
 **/

/**
 * 登录拦截器配置
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private UserInfoService userInfoService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取token
        String token = request.getHeader("Authorization");
        //更具token获取User
        User user = userInfoService.getUser(token);
        //判断是否为空
        if(user==null){
            //无权限操作
            response.setStatus(401);
        }
        //保存数据到ThreadLocal中
        UserHolder.setUser(user);
        return true;
    }
}
