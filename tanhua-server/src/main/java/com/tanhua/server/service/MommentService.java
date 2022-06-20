package com.tanhua.server.service;

import com.tanha.commons.exception.TanHuaException;
import com.tanha.commons.templates.OssTemplate;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.vo.ErrorResult;
import com.tanhua.domain.vo.MomentVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.PublishVo;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.mongo.PublishApi;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.utils.RelativeDateFormat;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/7/3 --星期六  下午 04:33
 **/
@Service
public class MommentService {

    @Reference
    private PublishApi publishApi;


    @Autowired
    private OssTemplate ossTemplate;


    @Reference
    private UserInfoApi userInfoApi;


    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 发布动态
     * @param publishVo
     * @param multipartFiles
     */
    public void movements(PublishVo publishVo, MultipartFile[] multipartFiles) {
     try{
         //查询当前用户id
         Long userId = UserHolder.getUserId();
         //创建集合存入图片路径
         List<String> medias = new ArrayList<>();
         //上传图片到云服务器
         if(multipartFiles!=null){
             for (MultipartFile multipartFile : multipartFiles) {
                 String upload = ossTemplate.upload(multipartFile.getOriginalFilename(), multipartFile.getInputStream());
                 medias.add(upload);
             }
         }
         //保存图片信息到对象里
         publishVo.setMedias(medias);
         //用户id
         publishVo.setUserId(userId);
         publishApi.add(publishVo);
     }catch (Exception e){
         e.printStackTrace();
         throw new TanHuaException(ErrorResult.error());
     }
    }

    /**
     * 好友动态
     * @param page
     * @param pagesizs
     * @return
     */
    public PageResult<MomentVo> queryFriendPublishList(int page, int pagesizs) {
        //查询时间线表
        //查询发布表
        //获取用户id
      return  getPageResult(publishApi.queryFriendPublishList(page,pagesizs,UserHolder.getUserId()));
    }

    /**
     * 推荐动态
     * @param page
     * @param pagesizs
     * @return
     */
    public PageResult<MomentVo> queryTuiJiaPublishList(int page, int pagesizs) {
        return getPageResult(publishApi.queryTuiJiaPublishList(page,pagesizs,UserHolder.getUserId()));
    }

    /**
     * 我的动态
     * @param page
     * @param pagesize
     * @param userId
     * @return
     */
    public PageResult<MomentVo> queryMyAlbum(int page, int pagesize, Long userId) {
        //通过用户id查询相册表 - 通过相册表查询发布表
       return getPageResult(publishApi.queryWoDePublishList(page,pagesize,userId));
    }

    /**
     * 动态列表共同操作
     * @param pageResult
     * @return
     */
    private PageResult getPageResult(PageResult pageResult){
        List<Publish> items = pageResult.getItems();
        //创建返回数据集合
        List<MomentVo> momentVos = new ArrayList<>();
        //遍历数据
        if(items!=null){

            for (Publish item : items) {
                MomentVo momentVo = new MomentVo();
                //复制对象
                BeanUtils.copyProperties(item,momentVo);
                //根据用户id查询用户信息
                UserInfo userInfo = userInfoApi.findUserInfoById(item.getUserId());
                if(userInfo!=null){
                    //复制对象
                    BeanUtils.copyProperties(userInfo,momentVo);
                    //判断标签是否为空
                    if(userInfo.getTags()!=null){
                        momentVo.setTags(userInfo.getTags().split(","));
                    }
                }
                momentVo.setId(item.getId().toHexString());
                momentVo.setCreateDate(RelativeDateFormat.format(new Date(item.getCreated())));
                String key = "publish_like_" + UserHolder.getUserId()+"_" + item.getId().toHexString();
                if(redisTemplate.hasKey(key)){
                    momentVo.setHasLiked(1);  //是否点赞  0：未点 1:点赞
                }else{
                    momentVo.setHasLiked(0);
                }
                String key1 = "publish_love_" + UserHolder.getUserId()+"_" + item.getId().toHexString();
                if(redisTemplate.hasKey(key1)){
                    momentVo.setHasLoved(1);
                }else {
                    momentVo.setHasLoved(0);  //是否喜欢  0：未点 1:点赞
                }
                momentVo.setImageContent(item.getMedias().toArray(new String[]{}));
                momentVo.setDistance("50米");
                momentVos.add(momentVo);
            }
        }
        pageResult.setItems(momentVos);
        return pageResult;
    }

    /**
     * 单个动态
     * @param publishId
     */
    public MomentVo findById(String publishId) {
        //获取用户id
        Long userId = UserHolder.getUserId();
        //创建MomentVo对象
        MomentVo momentVo = new MomentVo();
        Publish publish =  publishApi.findById(userId,publishId);
        if(publish!=null){
            BeanUtils.copyProperties(publish,momentVo);
            //获取个人用户信息
            if(publish.getUserId()!=null){
                UserInfo userInfo = userInfoApi.findUserInfoById(publish.getUserId());
                BeanUtils.copyProperties(userInfo,momentVo);
                if(userInfo.getTags()!=null){
                    momentVo.setTags(userInfo.getTags().split(","));
                }
            }
            momentVo.setId(publish.getId().toHexString());
            momentVo.setCreateDate(RelativeDateFormat.format(new Date(publish.getCreated())));
            momentVo.setHasLiked(0);  //是否点赞  0：未点 1:点赞
            momentVo.setHasLoved(0);  //是否喜欢  0：未点 1:点赞
            momentVo.setImageContent(publish.getMedias().toArray(new String[]{}));
            momentVo.setDistance("50米");
        }
        return momentVo;
    }
}
