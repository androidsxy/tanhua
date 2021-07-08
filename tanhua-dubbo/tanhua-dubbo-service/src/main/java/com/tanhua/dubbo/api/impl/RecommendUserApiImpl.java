package com.tanhua.dubbo.api.impl;

import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.RecommendUserApi;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.crypto.CipherSpi;
import java.util.List;


/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/7/1 --星期四  下午 07:05
 **/
@Service
public class RecommendUserApiImpl implements RecommendUserApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public RecommendUser findodayBestById(Long userId) {

        //条件Criteria criteria = Criteria.where("toUserId").is(toUserId);
        Query query = new Query();

        query.addCriteria(Criteria.where("toUserId").is(userId)).with(Sort.by(Sort.Order.desc("score"))).limit(1);

        return  mongoTemplate.findOne(query, RecommendUser.class);
    }

    /**
     * 推荐朋友
     * @param page
     * @param pagesize
     * @param userId
     * @return
     */
    @Override
    public PageResult findodayBestAll(Integer page, Integer pagesize, Long userId) {
        //条件
        Query query = new Query();
        query.addCriteria(Criteria.where("toUserId").is(userId));
        //查询总记录数
        long count = mongoTemplate.count(query, RecommendUser.class);
        //分页
        query.with(Sort.by(Sort.Order.desc("score"))).limit(pagesize).skip((page-1)*pagesize);
        //查询推荐表
        List<RecommendUser> recommendUsers = mongoTemplate.find(query, RecommendUser.class);
        //总页数
        long cont = count/pagesize;
        cont =cont + count%pagesize>1?1:0;
        //返回分页对象
        return new PageResult(cont,pagesize.longValue(),cont,page.longValue(),recommendUsers);
    }
}
