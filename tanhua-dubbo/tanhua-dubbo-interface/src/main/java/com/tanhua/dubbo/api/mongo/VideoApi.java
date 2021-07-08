package com.tanhua.dubbo.api.mongo;

import com.tanhua.domain.mongo.FollowUser;
import com.tanhua.domain.mongo.Video;
import com.tanhua.domain.vo.PageResult;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/7/8 --星期四  下午 04:52
 **/
public interface VideoApi {
    void save(Video video);

    PageResult findAllVideo(int page, int pageSize);

    void saveFollow(FollowUser followUser);

    void setFollowRemove(FollowUser followUser);
}
