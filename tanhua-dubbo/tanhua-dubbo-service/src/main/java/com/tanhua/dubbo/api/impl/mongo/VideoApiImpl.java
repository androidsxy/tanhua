package com.tanhua.dubbo.api.impl.mongo;

import com.tanhua.domain.mongo.FollowUser;
import com.tanhua.domain.mongo.Video;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.mongo.VideoApi;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/7/8 --星期四  下午 04:55
 **/
@Service
public class VideoApiImpl implements VideoApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 发布视频
     * @param video
     */
    @Override
    public void save(Video video) {
        //设置id
        video.setId(ObjectId.get());
        //设置上传时间
        video.setCreated(System.currentTimeMillis());
        //保存到数据库
        mongoTemplate.save(video);
    }

    /**
     * 查看小视频列表
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public PageResult findAllVideo(int page, int pageSize) {

        //条件
        Query query = new Query();
        //排序 分页
        query.with(Sort.by(Sort.Order.desc("created"))).limit(pageSize).skip((page-1)*pageSize);
        //总计数
        long count = mongoTemplate.count(query, Video.class);
        //查询小视频
        List<Video> videos = mongoTemplate.find(query, Video.class);

        long countq = count/pageSize;
        countq = countq + count%pageSize>0?1:0;

        return  new PageResult(count,Integer.toUnsignedLong(pageSize),countq,Integer.toUnsignedLong(page),videos);
    }

    /**
     * 关注
     * @param followUser
     */
    @Override
    public void saveFollow(FollowUser followUser) {
        //设置id
        followUser.setId(ObjectId.get());
        //设置条件时间
        followUser.setCreated(System.currentTimeMillis());
        //保存到mongodb
        mongoTemplate.save(followUser);
    }

    /**
     * 取消关注
     * @param followUser
     */
    @Override
    public void setFollowRemove(FollowUser followUser) {
        //删除条件
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(followUser.getUserId()).and("followUserId").is(followUser.getFollowUserId()));
        //删除
        mongoTemplate.remove(query,FollowUser.class);
    }
}
