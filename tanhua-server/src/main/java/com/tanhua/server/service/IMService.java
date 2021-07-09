package com.tanhua.server.service;

import com.alibaba.fastjson.JSON;
import com.tanha.commons.templates.HuanXinTemplate;
import com.tanhua.domain.db.Question;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.mongo.Friend;
import com.tanhua.domain.vo.ContactVo;
import com.tanhua.domain.vo.MessageVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.QuestionApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.mongo.CommentApi;
import com.tanhua.dubbo.api.mongo.FriendApi;
import com.tanhua.server.interceptor.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/7/9 --星期五  下午 06:12
 **/

@Service
@Slf4j
public class IMService {

    @Reference
    private UserInfoApi userInfoApi;

    @Reference
    private QuestionApi questionApi;

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @Reference
    private FriendApi friendApi;

    @Reference
    private CommentApi commentApi;



    /**
     * 回复陌生人信息
     * @param userId
     * @param reply
     */
    public void replyStrangerQuestions(long userId, String reply) {
        //获取当前用户信息
        UserInfo userInfo = userInfoApi.findUserInfoById(UserHolder.getUserId());
        //查询陌生人问题
        Question question = questionApi.findQuestionById(userId);

        //创建map对象
        Map<String,String> map = new HashMap<>();
        map.put("userId", userInfo.getId().toString());
        map.put("nickname", userInfo.getNickname());
        map.put("strangerQuestion", question==null?"你喜欢我吗？":question.getTxt());
        map.put("reply", reply);

        //把map数据转成json数据
        String message = JSON.toJSONString(map);

        //发送信息
        huanXinTemplate.sendMsg(userId+"",message);
    }

    /**
     * 添加联系人
     * @param friendId
     */
    public void addUser(Long friendId) {
        //获取当前用户信息
        Long userId = UserHolder.getUserId();
        //添加信息到数据库
        friendApi.addUser(userId,friendId);
        //环信添加好友
        huanXinTemplate.makeFriends(userId,friendId);
    }

    /**
     * 查询联系人列表
     * @param page
     * @param pageSize
     * @return
     */
    public PageResult<ContactVo> queryContactsList(int page, int pageSize) {
        //获取当前用户信息
        Long userId = UserHolder.getUserId();
        //获取联系人列表
        PageResult pageResult = friendApi.findAllFriend(page,pageSize,userId);
        //获取分页数据
        List<Friend> items = pageResult.getItems();
        //创建空集合保存返回数据
        List<ContactVo> contactVos = new ArrayList<>();
        if(pageResult==null|| items ==null){
            return null;
        }
        //循环遍历
        for (Friend friend : items) {
            ContactVo contactVo = new ContactVo();
            //查询个人形象
            UserInfo userInfo = userInfoApi.findUserInfoById(friend.getFriendId());
            //复制对象
            BeanUtils.copyProperties(userInfo,contactVo);
            contactVo.setUserId(userInfo.getId().toString());
            contactVo.setCity(StringUtils.substringBefore(userInfo.getCity(),"-"));
            contactVos.add(contactVo);
        }
        pageResult.setItems(contactVos);
        return pageResult;
    }

    /**
     * 点赞  评论  喜欢 列表
     * @param page
     * @param pageSize
     * @param i
     * @return
     */
    public PageResult<MessageVo> counts(int page, int pageSize, int i) {
        //获取当前用户id
        Long userId = UserHolder.getUserId();
        //获取分页列表
        PageResult pageResult = commentApi.findAllById(page,pageSize,i,userId);
        //获取分页数据
        List<Comment> items = pageResult.getItems();
        //创建空集合返回对象
        List<MessageVo> messageVoList = new ArrayList<>();
        if(items==null){
            return null;
        }
        for (Comment item : items) {
            //创建返回对象
            MessageVo messageVo = new MessageVo();
            //获取个人信息
            UserInfo userInfo = userInfoApi.findUserInfoById(item.getUserId());
            //复制对象
            BeanUtils.copyProperties(userInfo,messageVo);
            messageVo.setId(item.getId().toHexString());
            messageVo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(item.getCreated())));
            messageVoList.add(messageVo);
        }
        pageResult.setItems(messageVoList);
        return pageResult;
    }
}
