package com.tanhua.dubbo.api.impl.mongo;

import com.tanhua.domain.mongo.*;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.PublishVo;
import com.tanhua.dubbo.api.mongo.PublishApi;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;


/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/7/3 --星期六  下午 04:49
 **/
@Service
public class PublishApiImpl implements PublishApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 发布动态
     * @param publishVo
     */
    @Override
    public void add(PublishVo publishVo) {
        //当前时间
        long time = System.currentTimeMillis();
        //1 插入数据到发布表
        Publish publish = new Publish();
        //复制对象
        BeanUtils.copyProperties(publishVo,publish);
        //封装数据
        publish.setId(ObjectId.get());
        //地理位置
        publish.setLocationName(publishVo.getLocation());
        //公开
        publish.setSeeType(1);
        //插入时间
        publish.setCreated(time);
        //保存数据
        mongoTemplate.save(publish);
        //2 插入数据到相册表
        Album album = new Album();
        album.setId(ObjectId.get());
        album.setPublishId(publish.getId());
        album.setCreated(time);
        //相册表保存数据
        mongoTemplate.save(album,"quanzi_album_" + publish.getUserId());
        //3 查询好友表
        //查询条件
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(publishVo.getUserId()));
        List<Friend> fields = mongoTemplate.find(query, Friend.class);
        //判断好友是否为空
        if(fields!=null){
            for (Friend field : fields) {
                //4 插入数据到好友时间线表
                TimeLine timeLine = new TimeLine();
                timeLine.setId(ObjectId.get());
                timeLine.setPublishId(publish.getId());
                timeLine.setCreated(time);
                //好友id里的
                timeLine.setUserId(field.getUserId());
                mongoTemplate.save(timeLine,"quanzi_time_line_" + field.getFriendId());
            }
        }
    }

    /**
     * 好友动态
     * @param page
     * @param pagesizs
     * @param userId
     * @return
     */
    @Override
    public PageResult queryFriendPublishList(int page, int pagesizs, Long userId) {
        //条件
        Query query = new Query();
        query.with(Sort.by(Sort.Order.desc("created"))).limit(pagesizs).skip((page-1)*pagesizs);
        //总记录数
        long count1 = mongoTemplate.count(query, TimeLine.class);
        //查询时间线表
        List<TimeLine> timeLines = mongoTemplate.find(query, TimeLine.class, "quanzi_time_line_" + userId);
        //创建发布表集合
        List<Publish> list = new ArrayList<>();
        //查询分布表
        if(timeLines!=null){
            for (TimeLine timeLine : timeLines) {
                if(timeLine.getPublishId()!=null){
                 //查询发布表
                    Publish publish = mongoTemplate.findById(timeLine.getPublishId(), Publish.class);
                    if(publish!=null){
                        list.add(publish);
                    }
                }
            }
        }
        long count = count1/pagesizs;
        count = count + count1%pagesizs>0?1:0;

        return  new PageResult(count1,Integer.toUnsignedLong(pagesizs),count,Integer.toUnsignedLong(page),list);
    }

    /**
     * 推荐表
     * @param page
     * @param pagesizs
     * @param userId
     * @return
     */
    @Override
    public PageResult queryTuiJiaPublishList(int page, int pagesizs, Long userId) {
        //条件
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId))
                .limit(pagesizs).skip((page-1)*pagesizs);

        //查询总记录数
        long count = mongoTemplate.count(query, RecommendQuanzi.class);
        //查询分页
        List<RecommendQuanzi> recommendQuanzis = mongoTemplate.find(query, RecommendQuanzi.class);
        //创建发布id分页
        List<Publish> publishes = new ArrayList<>();
        //判断是否为空
        if(recommendQuanzis!=null){
            for (RecommendQuanzi recommendQuanzi : recommendQuanzis) {
                //根据发布id查询发布数据
                if(recommendQuanzi.getPublishId()!=null){
                    Publish byId = mongoTemplate.findById(recommendQuanzi.getPublishId(), Publish.class);
                    if(byId!=null){
                        publishes.add(byId);
                    }
                }
            }
        }
        long countq = count/pagesizs;
        countq = countq + count%pagesizs>0?1:0;

        return  new PageResult(count,Integer.toUnsignedLong(pagesizs),countq,Integer.toUnsignedLong(page),publishes);
    }


    /**
     * 我的动态
     * @param page
     * @param pagesize
     * @param userId
     * @return
     */
    @Override
    public PageResult queryWoDePublishList(int page, int pagesize, Long userId) {

        //查询条件
        Query query = new Query();
        query.with(Sort.by(Sort.Order.desc("created"))).limit(pagesize).skip((page-1)*pagesize);
        //总记录数
        long count = mongoTemplate.count(query, Album.class, "quanzi_album_" + userId);
        //相册列表
        List<Album> list = mongoTemplate.find(query, Album.class, "quanzi_album_" + userId);
        //创建一个空的集合对象
        List<Publish> list1 = new ArrayList<>();
        //循环
        if(list!=null){
            //查询发布表
            for (Album album : list) {
                if(album.getPublishId()!=null){
                    Publish publish = mongoTemplate.findById(album.getPublishId(), Publish.class);
                    if(publish!=null){
                        list1.add(publish);
                    }
                }
            }

        }
        long countq = count/pagesize;
        countq = countq + count%pagesize>0?1:0;

        return  new PageResult(count,Integer.toUnsignedLong(pagesize),countq,Integer.toUnsignedLong(page),list1);
    }

    /**
     * 个人动态
     * @param userId
     * @param publishId
     * @return
     */
    @Override
    public Publish findById(Long userId, String publishId) {
        //条件
        return mongoTemplate.findById(publishId, Publish.class);
    }
}
