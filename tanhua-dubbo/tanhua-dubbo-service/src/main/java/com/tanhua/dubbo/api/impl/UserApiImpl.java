package com.tanhua.dubbo.api.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.domain.db.User;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.mapper.UserMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/6/24 --星期四  下午 01:37
 **/
@Service
public class UserApiImpl implements UserApi {

    @Autowired
    private UserMapper userMapper;

    /**
     * 添加用户
     * @param user
     * @return
     */
    @Override
    public Long save(User user) {
        //插入创建时间
        user.setCreated(new Date());
        //插入更新时间
        user.setUpdated(new Date());
        //添加用户
        userMapper.insert(user);
        //返回用户id
        return user.getId();
    }

    /**
     * 通过手机号码查询用户
     * @param mobile
     * @return
     */
    @Override
    public User findByMobile(String mobile) {
        //创建条件类
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //调用等于方法
        queryWrapper.eq("mobile",mobile);
        //返回查询出来的数据
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 更新手机号
     * @param user
     */
    @Override
    public void update(User user) {
        userMapper.updateById(user);
    }
}
