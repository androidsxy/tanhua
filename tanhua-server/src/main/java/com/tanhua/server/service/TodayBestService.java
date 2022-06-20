package com.tanhua.server.service;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.RecommendUserQueryParam;
import com.tanhua.domain.vo.TodayBestVo;
import com.tanhua.dubbo.api.RecommendUserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.commons.lang3.RandomUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 推荐朋友
     * @param queryParam
     * @return
     */
    public PageResult<TodayBestVo> recommendation(RecommendUserQueryParam queryParam) {
        //获取当前用户id
        Long userId = UserHolder.getUserId();
        //查询推荐好友
        PageResult result = recommendUserApi.findodayBestAll(queryParam.getPage(),queryParam.getPagesize(),userId);
        //获取分页集合
        List<RecommendUser> items = result.getItems();
        //创建一个空的集合封装数据
        List<TodayBestVo> list = new ArrayList<>();
        // 如果未查询到，需要使用默认推荐列表
        if (CollectionUtils.isEmpty(items)) {
            result = new PageResult(10l,queryParam.getPagesize().longValue(),1l,1l,null);
            items =  defaultRecommend();
        }
        //遍历数据
            for (RecommendUser item : items) {
                //创建封装对象
                TodayBestVo todayBestVo = new TodayBestVo();
                //通过推荐id查询用户数据
                UserInfo userInfo = userInfoApi.findUserInfoById(item.getUserId());
                //id
                todayBestVo.setId(item.getUserId());
                //缘分值
                todayBestVo.setFateValue(item.getScore().longValue());
                if(userInfo.getTags()!=null){
                    todayBestVo.setTags(userInfo.getTags().split(","));
                }
                BeanUtils.copyProperties(userInfo,todayBestVo);
                list.add(todayBestVo);
            }

        result.setItems(list);
        return result;

    }
    //构造默认数据
    private List<RecommendUser> defaultRecommend() {
        String ids = "1,2,3,4,5,6,7,8,9,10";
        List<RecommendUser> records = new ArrayList<>();
        for (String id : ids.split(",")) {
            RecommendUser recommendUser = new RecommendUser();
            recommendUser.setUserId(Long.valueOf(id));
            recommendUser.setScore(RandomUtils.nextDouble(70, 98));
            records.add(recommendUser);
        }
        return records;
    }
}
