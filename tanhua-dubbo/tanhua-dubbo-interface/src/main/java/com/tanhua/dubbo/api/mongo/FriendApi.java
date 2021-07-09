package com.tanhua.dubbo.api.mongo;

import com.tanhua.domain.vo.PageResult;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/7/9 --星期五  下午 07:26
 **/
public interface FriendApi {
    void addUser(Long userId, Long friendId);

    PageResult findAllFriend(int page, int pageSize, Long userId);
}
