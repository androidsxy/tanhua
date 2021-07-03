package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.vo.PageResult;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/7/1 --星期四  下午 07:03
 **/
public interface RecommendUserApi {
    RecommendUser findodayBestById(Long userId);

    PageResult findodayBestAll(Integer page, Integer pagesize, Long userId);
}
