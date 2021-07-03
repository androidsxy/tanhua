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
        Long userId = UserHolder.getUserId();
        PageResult result  =  publishApi.queryFriendPublishList(page,pagesizs,userId);
        //获取分页数据
        List<Publish> items = result.getItems();
        //创建空对象存储值
        List<MomentVo> momentVos = new ArrayList<>();
        //遍历
        if(items!=null){
            for (Publish item : items) {
                MomentVo momentVo = new MomentVo();
                //复制对象
                BeanUtils.copyProperties(item,momentVo);
                //通过用户di查询用户数据
                UserInfo userInfo = userInfoApi.findUserInfoById(item.getUserId());
                if(userInfo!=null){
                    //复制对象
                    BeanUtils.copyProperties(userInfo,momentVo);
                    if(userInfo.getTags()!=null){
                        momentVo.setTags(userInfo.getTags().split(","));
                    }
                }
                momentVo.setId(item.getId().toHexString());
                momentVo.setCreateDate(RelativeDateFormat.format(new Date(item.getCreated())));
                momentVo.setHasLiked(0);  //是否点赞  0：未点 1:点赞
                momentVo.setHasLoved(0);  //是否喜欢  0：未点 1:点赞
                momentVo.setImageContent(item.getMedias().toArray(new String[]{}));
                momentVo.setDistance("50米");
                momentVos.add(momentVo);
            }
        }
        result.setItems(momentVos);

        return result;
    }

    /**
     *
     * @param page
     * @param pagesizs
     * @return
     */
    public PageResult<MomentVo> queryTuiJiaPublishList(int page, int pagesizs) {
        //用户id
        Long userId = UserHolder.getUserId();
        //获取分布表分页
        PageResult pageResult =  publishApi.queryTuiJiaPublishList(page,pagesizs,userId);
        //获取分页数据
        List<Publish> items = pageResult.getItems();
        //创建一个空的List
        List<MomentVo> momentVos = new ArrayList<>();
        //判断是否空
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
                momentVo.setHasLiked(0);  //是否点赞  0：未点 1:点赞
                momentVo.setHasLoved(0);  //是否喜欢  0：未点 1:点赞
                momentVo.setImageContent(item.getMedias().toArray(new String[]{}));
                momentVo.setDistance("50米");
                momentVos.add(momentVo);
            }
        }

        pageResult.setItems(momentVos);
        return pageResult;
    }
}
