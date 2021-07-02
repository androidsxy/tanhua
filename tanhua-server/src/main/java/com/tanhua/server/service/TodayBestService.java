package com.tanhua.server.service;

import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.vo.TodayBestVo;
import com.tanhua.dubbo.api.RecommendUserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/7/1 --星期四  下午 06:43
 **/

@Service
public class TodayBestService {

    @Reference
    private RecommendUserApi recommendUserApi;


    @Reference
    private UserInfoApi userInfoApi;
    /**
     * 今日佳人
     * @return
     */
    public TodayBestVo findtodayBest() {
        //获取用户id
        Long userId = UserHolder.getUserId();
        //查询今日佳人
        RecommendUser recommendUser  = recommendUserApi.findodayBestById(userId);
        //判断是否存在
        if(recommendUser==null){
            recommendUser = new RecommendUser();
            recommendUser.setUserId(10L);
            recommendUser.setScore(99d);
        }
        //查询用户信息
        UserInfo userInfoById = userInfoApi.findUserInfoById(recommendUser.getUserId());
        //构造返回值对象
        TodayBestVo todayBestVo = new TodayBestVo();
        //复制
        BeanUtils.copyProperties(userInfoById,todayBestVo);
        //标签
        todayBestVo.setTags(userInfoById.getTags().split(","));
        //缘分值
        todayBestVo.setFateValue(recommendUser.getScore().longValue());

        return todayBestVo;
    }
}
