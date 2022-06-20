package com.tanhua.server.service;

import com.tanhua.domain.db.User;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.vo.CommentVo;
import com.tanhua.domain.vo.MomentVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.mongo.CommentApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.bson.types.ObjectId;
import org.checkerframework.checker.units.qual.C;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/7/4 --星期日  下午 05:21
 **/

@Service
public class CommentService {

    @Reference
    private CommentApi commentApi;

    @Reference
    private UserInfoApi userInfoApi;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 动态点赞
     * @param publishId
     * @return
     */
    public Long like(String publishId) {
        //获取当前用户id
        Long userId = UserHolder.getUserId();
        //创建评论对象
        Comment comment = new Comment();
        //设置分布表id
        comment.setPublishId(new ObjectId(publishId));
        //设置用户id
        comment.setUserId(userId);
        //设置评论类型 1点赞  2评论  3喜欢
        comment.setCommentType(1);
        //设置对动态操作
        comment.setPubType(1);
        //保存数据到评论表 更新分布表数据 返回记录数
        Long  total  = commentApi.save(comment);
        //把点赞炒作存入redis
        String key = "publish_like_" + userId+"_" + publishId;
        redisTemplate.opsForValue().set(key,"0");
        return total;
    }

    /**
     * 动态取消
     * @param publishId
     * @return
     */
    public Long dislike(String publishId) {
        //获取当前用户id
        Long userId = UserHolder.getUserId();
        //创建评论对象
        Comment comment = new Comment();
        //设置分布表id
        comment.setPublishId(new ObjectId(publishId));
        //设置用户id
        comment.setUserId(userId);
        //设置评论类型 1点赞  2评论  3喜欢
        comment.setCommentType(1);
        //设置对动态操作
        comment.setPubType(1);
        //删除评论表数据 更新分布表数据 返回记录数
        Long  total  = commentApi.remove(comment);
        //删除redis里的点赞数据
        String key = "publish_like_" + userId+"_" + publishId;
        redisTemplate.delete(key);
        return total;
    }

    /**
     * 喜欢
     * @param publishId
     * @return
     */
    public Long love(String publishId) {
        //获取当前用户id
        Long userId = UserHolder.getUserId();
        //创建评论对象
        Comment comment = new Comment();
        //设置分布表id
        comment.setPublishId(new ObjectId(publishId));
        //设置用户id
        comment.setUserId(userId);
        //设置评论类型 1点赞  2评论  3喜欢
        comment.setCommentType(3);
        //设置对动态操作
        comment.setPubType(1);
        //保存数据到评论表 更新分布表数据 返回记录数
        Long  total  = commentApi.save(comment);
        String key = "publish_love_" + userId+"_" + publishId;
        // 记录下点了赞了
        redisTemplate.opsForValue().set(key,"1");
        return total;
    }

    /**
     * 取消喜欢
     * @param publishId
     * @return
     */
    public Long unlove(String publishId) {
        //获取当前用户id
        Long userId = UserHolder.getUserId();
        //创建评论对象
        Comment comment = new Comment();
        //设置分布表id
        comment.setPublishId(new ObjectId(publishId));
        //设置用户id
        comment.setUserId(userId);
        //设置评论类型 1点赞  2评论  3喜欢
        comment.setCommentType(3);
        //设置对动态操作
        comment.setPubType(1);
        //删除评论表数据 更新分布表数据 返回记录数
        Long  total  = commentApi.remove(comment);
        //删除redis里的点赞数据
        String key = "publish_love_" + userId+"_" + publishId;
        redisTemplate.delete(key);
        return total;
    }

    /**
     * 获取评论列表
     * @param movementId
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult<CommentVo> findAllCommentList(String movementId, int page, int pagesize) {
        //获取评论列表
        PageResult pageResult = commentApi.findAllCommentList(page,pagesize,movementId);
        //获取评论分页数据
        List<Comment> items = pageResult.getItems();
        //创建list对象
        List<CommentVo> list = new ArrayList<>();
        //遍历
        if(items!=null){
            for (Comment item : items) {
                CommentVo commentVo = new CommentVo();
                BeanUtils.copyProperties(item,commentVo);
                //获取用户信息
                UserInfo userInfo = userInfoApi.findUserInfoById(item.getUserId());
                //复制对象
                BeanUtils.copyProperties(userInfo,commentVo);
                //设置评论id
                commentVo.setId(item.getId().toHexString());
                commentVo.setCreateDate(new DateTime(item.getCreated()).toString("yyyy年MM月dd日 HH:mm"));
                String key = "comment_like_" + UserHolder.getUserId()+"_" + item.getId().toHexString();
                // 记录下点了赞了
                if(redisTemplate.hasKey(key)){
                    commentVo.setHasLiked(1);//是否点赞
                }
                else {
                    commentVo.setHasLiked(0);//是否点赞
                }
                list.add(commentVo);
            }
        }
        pageResult.setItems(list);
        return pageResult;
    }

    /**
     * 发表评论
     * @param paramMap
     */
    public void add(Map<String, String> paramMap) {
        //获取单曲用户
        Long userId = UserHolder.getUserId();
        //获取数据
        String movementId = paramMap.get("movementId");
        String comment = paramMap.get("comment");
        //创建对象
        Comment comment1 = new Comment();
        comment1.setUserId(userId);
        comment1.setPubType(1);
        comment1.setCommentType(2);
        comment1.setContent(comment);
        comment1.setPublishId(new ObjectId(movementId));
        //添加数据
        commentApi.save(comment1);
    }

    /**
     * 评论点赞
     * @param commentId
     * @return
     */
    public Long likeComment(String commentId) {
        //获取当前用户id
        Long userId = UserHolder.getUserId();
        //创建评论对象
        Comment comment = new Comment();
        //设置分布表id
        comment.setPublishId(new ObjectId(commentId));
        //设置用户id
        comment.setUserId(userId);
        //设置评论类型 1点赞  2评论  3喜欢
        comment.setCommentType(1);
        //设置对动态操作
        comment.setPubType(3);
        //删除评论表数据 更新分布表数据 返回记录数
        Long  total  = commentApi.save(comment);
        String key = "comment_like_" + userId+"_" + commentId;
        // 记录下点了赞了
        redisTemplate.opsForValue().set(key,"1");
        return total;
    }

    /**
     * 评论取消
     * @param commentId
     * @return
     */
    public Long unlikeComment(String commentId) {
        //获取当前用户id
        Long userId = UserHolder.getUserId();
        //创建评论对象
        Comment comment = new Comment();
        //设置分布表id
        comment.setPublishId(new ObjectId(commentId));
        //设置用户id
        comment.setUserId(userId);
        //设置评论类型 1点赞  2评论  3喜欢
        comment.setCommentType(1);
        //设置对动态操作
        comment.setPubType(3);
        //删除评论表数据 更新分布表数据 返回记录数
        Long  total  = commentApi.remove(comment);
        String key = "comment_like_" + userId+"_" + commentId;
        // 移除redis中记录
        redisTemplate.delete(key);
        return total;
    }
}
