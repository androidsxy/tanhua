package com.tanhua.dubbo.api;

import com.tanhua.domain.db.User;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/6/24 --星期四  下午 01:32
 **/
public interface UserApi {
    /**
     * 添加用户
     * @param user
     * @return
     */
    Long save(User user);

    /**
     * 通过手机号查询
     * @param mobile
     * @return
     */
    User findByMobile(String mobile);

    /**
     * 更新手机号
     * @param user
     */
    void update(User user);
}
