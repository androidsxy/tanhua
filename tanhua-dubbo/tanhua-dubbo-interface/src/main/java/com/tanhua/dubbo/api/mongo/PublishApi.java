package com.tanhua.dubbo.api.mongo;

import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.PublishVo;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/7/3 --星期六  下午 04:47
 **/
public interface PublishApi {
    void add(PublishVo publishVo);

    PageResult queryFriendPublishList(int page, int pagesizs, Long userId);

    PageResult queryTuiJiaPublishList(int page, int pagesizs, Long userId);
}
