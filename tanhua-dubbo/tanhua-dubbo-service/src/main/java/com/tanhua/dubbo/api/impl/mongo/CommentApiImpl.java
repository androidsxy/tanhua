package com.tanhua.dubbo.api.impl.mongo;

import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.mongo.CommentApi;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;


/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/7/4 --星期日  下午 05:35
 **/
@Service
public class CommentApiImpl implements CommentApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 动态点赞 /评论 /喜欢操作
     * @param comment
     * @return
     */
    @Override
    public Long save(Comment comment) {
        //保存数据到评论表
        comment.setId(ObjectId.get());
        //时间
        comment.setCreated(System.currentTimeMillis());
        //插入到数据库
        mongoTemplate.save(comment);
        //更新发布表或评论表
        getUpdate(comment,1);
        //获取记录数
        Long total = getCount(comment);
        return total;
    }

    /**
     * 动态更新操作
     * @param comment
     * @param index
     */
    private void getUpdate(Comment comment,int index) {
        //条件
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(comment.getPublishId()));
        Update update = new Update();
        update.inc(comment.getCol(),index);
        Class<?> publishClass = Publish.class;
        if(comment.getPubType()==3){
            publishClass = Comment.class;
        }
        //更新表
        mongoTemplate.updateFirst(query,update,publishClass);
    }

    /**
     * 返回动态数据
     * @param comment
     * @return
     */
    private Long getCount(Comment comment){
        //条件
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(comment.getPublishId()));
        if(comment.getPubType()==1){
            //动态操作
            Publish publish = mongoTemplate.findOne(query, Publish.class);
            //点赞
            if(comment.getCommentType()==1){
                return (long)publish.getLikeCount();
            }
            //评论
            if(comment.getCommentType()==2){
                return (long)publish.getCommentCount();
            }
            //喜欢
            if(comment.getCommentType()==3){
                return (long)publish.getLoveCount();
            }
        }
        //对评论操作
        if(comment.getPubType()==3){
            Comment comment1 = mongoTemplate.findOne(query, Comment.class);

            return (long)comment1.getLikeCount();
        }
        return 99l;
    }

    /**
     * 取消 点赞/ 喜欢？
     * @param comment
     * @return
     */
    @Override
    public Long remove(Comment comment) {
        // 删除comment
        Query removeQuery = new Query();
        removeQuery.addCriteria(Criteria.where("publishId").is(comment.getPublishId())
                .and("commentType").is(comment.getCommentType())
                .and("userId").is(comment.getUserId()));
        //删除数据
        mongoTemplate.remove(removeQuery,Comment.class);
        // 计数减去1
        getUpdate(comment,-1);
        // 统计数量
        Long count = getCount(comment);
        return count;
    }

    @Override
    public PageResult findAllCommentList(int page, int pagesize, String movementId) {
        //条件
        Query query = new Query();
        query.addCriteria(Criteria.where("publishId").is(new ObjectId(movementId))
                .and("commentType").is(2))
                .with(Sort.by(Sort.Order.desc("created"))).limit(pagesize).skip((page-1)*pagesize);
        //获取总记录数
        long count = mongoTemplate.count(query, Comment.class);
        //分页数据
        List<Comment> comments = mongoTemplate.find(query, Comment.class);

        long countq = count/pagesize;
        countq = countq + count%pagesize>0?1:0;

        return  new PageResult(count,Integer.toUnsignedLong(pagesize),countq,Integer.toUnsignedLong(page),comments);
    }
}
