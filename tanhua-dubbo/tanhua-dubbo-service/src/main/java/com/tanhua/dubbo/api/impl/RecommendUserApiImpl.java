package com.tanhua.dubbo.api.impl;

import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.dubbo.api.RecommendUserApi;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.crypto.CipherSpi;


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
}
