package com.tanhua.dubbo.api;

import com.tanhua.domain.db.UserInfo;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/6/27 --星期日  下午 04:58
 **/
public interface UserInfoApi {

    /**
     * 完善个人信息
     * @param userInfo
     */
    void saveUserInfo(UserInfo userInfo);

    void saveUserInfoUpdate(UserInfo userInfo);

    UserInfo findUserInfoById(Long userId);
}
