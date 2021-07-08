package com.tanhua.server.service;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanha.commons.exception.TanHuaException;
import com.tanha.commons.templates.OssTemplate;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.FollowUser;
import com.tanhua.domain.mongo.Video;
import com.tanhua.domain.vo.ErrorResult;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.VideoVo;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.mongo.VideoApi;
import com.tanhua.server.interceptor.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/7/8 --星期四  下午 04:36
 **/

/**
 * 小视频业务层
 */
@Service
@Slf4j
public class VideoService {

    @Reference
    private VideoApi videoApi;

    @Autowired
    private OssTemplate ossTemplate;


    @Autowired
    private FastFileStorageClient client;

    @Autowired
    private FdfsWebServer fdfsWebServer;

    @Reference
    private UserInfoApi userInfoApi;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 发布小视频
     * @param videoFile
     * @param videoThumbnail
     */
    public void saveVideoService(MultipartFile videoFile, MultipartFile videoThumbnail) {
      try{
          //获取当前用户id
          Long userId = UserHolder.getUserId();
          //上传小视频图片到阿里云
          String videoPurl = ossTemplate.upload(videoThumbnail.getOriginalFilename(), videoThumbnail.getInputStream());
          //获取视频文件名
          String originalFilename = videoFile.getOriginalFilename();
          //获取后缀名
          String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
          //上传文件到fastDFS
          StorePath storePath = client.uploadFile(videoFile.getInputStream(), videoFile.getSize(), suffix, null);
          //视频url
          String videoUrl = fdfsWebServer.getWebServerUrl() + storePath.getFullPath();
          //创建对象保存数据
          Video video = new Video();
          video.setUserId(userId);
          video.setPicUrl(videoPurl);
          video.setVideoUrl(videoUrl);
          video.setText("黑马小视频");
          //保存数据到数据库
          videoApi.save(video);

      }catch (Exception e){
          e.printStackTrace();
          throw new TanHuaException(ErrorResult.error());
      }
    }

    /**
     * 小视频列表
     * @param page
     * @param pageSize
     * @return
     */
    public PageResult<VideoVo> findAllVideo(int page, int pageSize) {
        //查询小视频
        PageResult pageResult = videoApi.findAllVideo(page,pageSize);
        //获取到分页数据
        List<Video> videos = pageResult.getItems();
        //创建对象保存返回数据
        List<VideoVo> videoVoList = new ArrayList<>();
        //遍历数据
        if(videos!=null){
            for (Video video : videos) {
                //创建返回对象
                VideoVo videoVo = new VideoVo();
                //复制对象
                BeanUtils.copyProperties(video,videoVo);
                //获取个人用户信息
                UserInfo userInfo = userInfoApi.findUserInfoById(video.getUserId());
                //复制对象
                BeanUtils.copyProperties(userInfo,videoVo);
                //设置封面文件
                videoVo.setCover(video.getPicUrl());
                //设置id
                videoVo.setId(video.getId().toHexString());
                //标签
                if(userInfo.getTags()!=null){
                    videoVo.setSignature(userInfo.getTags());
                } else {
                    videoVo.setSignature("默认签名");//签名
                }
                String key = "video_follow_"+UserHolder.getUserId()+"_"+video.getUserId();
                if(redisTemplate.hasKey(key)){
                    videoVo.setHasFocus(1); //TODO 是否关注
                }else{
                    videoVo.setHasFocus(0);
                }

                videoVo.setHasLiked(0); //是否点赞
                videoVoList.add(videoVo);
            }
        }
        pageResult.setItems(videoVoList);
        return pageResult;
    }

    /**
     * 关注用户
     * @param followUserId
     */
    public void userFocus(String followUserId) {
        //获取当前用户id
        Long userId = UserHolder.getUserId();
        //创建对象
        FollowUser followUser = new FollowUser();
        //设置当前用户id
        followUser.setUserId(userId);
        //设置被关注的id
        followUser.setFollowUserId(Long.parseLong(followUserId));
        //添加到数据库
        videoApi.saveFollow(followUser);
        //保存数据到 redis
        String key = "video_follow_"+userId+"_"+followUserId;
        redisTemplate.opsForValue().set(key,"0");
    }

    /**
     * 取消关注
     * @param followUserId
     */
    public void userUnFocus(String followUserId) {
        //获取当前用户id
        Long userId = UserHolder.getUserId();
        //创建对象
        FollowUser followUser = new FollowUser();
        //设置当前用户id
        followUser.setUserId(userId);
        //设置被关注的id
        followUser.setFollowUserId(Long.parseLong(followUserId));
        //取消关注
        videoApi.setFollowRemove(followUser);
        //删除redis里记录
        String key = "video_follow_"+userId+"_"+followUserId;
        redisTemplate.delete(key);
    }
}
