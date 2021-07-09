package com.tanhua.dubbo.api.impl.mongo;

import com.tanhua.domain.mongo.Friend;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.mongo.FriendApi;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/7/9 --星期五  下午 07:29
 **/
@Service
public class FriendApiImpl implements FriendApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 添加好友
     * @param userId
     * @param friendId
     */
    @Override
    public void addUser(Long userId, Long friendId) {
        //条件
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId)
        .and("friendId").is(friendId));
        //查询是否存在好友关系
        boolean exists = mongoTemplate.exists(query, Friend.class);
        if(!exists){
            //不存在好友关系
            Friend friend = new Friend();
            friend.setId(ObjectId.get());
            friend.setFriendId(friendId);
            friend.setUserId(userId);
            friend.setCreated(System.currentTimeMillis());
            mongoTemplate.save(friend);
        }
        //查询当前登录账号是否是对方好友
        query = new Query();
        query.addCriteria(Criteria.where("friendId").is(userId)
                .and("userId").is(friendId));
        if(!mongoTemplate.exists(query,Friend.class)){
            Friend friend = new Friend();
            friend.setCreated(System.currentTimeMillis());
            friend.setUserId(friendId);
            friend.setFriendId(userId);
            friend.setId(ObjectId.get());
            mongoTemplate.save(friend);
        }

    }

    /**
     * 查询联系人列表
     * @param page
     * @param pageSize
     * @param userId
     * @return
     */
    @Override
    public PageResult findAllFriend(int page, int pageSize, Long userId) {
        //条件
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        query.limit(pageSize).skip((page-1)*pageSize);
        //获取记录数
        long count = mongoTemplate.count(query, Friend.class);
        //获取分页数据
        List<Friend> friends = mongoTemplate.find(query, Friend.class);

        //总页数
        long countq = count/pageSize;
        countq = countq + count%pageSize>0?1:0;

        return  new PageResult(count,Integer.toUnsignedLong(pageSize),countq,Integer.toUnsignedLong(page),friends);
    }
}
