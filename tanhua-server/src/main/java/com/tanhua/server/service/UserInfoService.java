package com.tanhua.server.service;

import com.alibaba.fastjson.JSON;
import com.tanha.commons.exception.TanHuaException;
import com.tanha.commons.templates.FaceTemplate;
import com.tanha.commons.templates.OssTemplate;
import com.tanhua.domain.db.User;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.ErrorResult;
import com.tanhua.domain.vo.UserInfoVo;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.utils.GetAgeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/6/27 --星期日  下午 04:27
 **/
@Service
public class UserInfoService {


    @Value("${tanhua.uid}")
    private String uid;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Reference
    private UserInfoApi userInfoApi;


    @Autowired
    private OssTemplate ossTemplate;


    @Autowired
    private FaceTemplate faceTemplate;

    /**
     * 填写个人信息
     * @param userInfoVo
     */
    public void saveUserInfo(UserInfoVo userInfoVo) {
        //创建对象
        UserInfo userInfo = new UserInfo();
        //复制当前对象
        BeanUtils.copyProperties(userInfoVo,userInfo);
        //设置id
        userInfo.setId(UserHolder.getUserId());
        //设置年龄
        userInfo.setAge(GetAgeUtil.getAge(userInfo.getBirthday()));
        //更新信息
        userInfoApi.saveUserInfo(userInfo);
    }

    /**
     * 获取登录信息
     * @param token
     * @return
     */
    public User getUser(String token){
        //获取redis里面保存的数据
        String userString = redisTemplate.opsForValue().get(uid + token);
        if(StringUtils.isEmpty(userString)){
            //验证码码失效
            throw new TanHuaException(ErrorResult.error());
        }
        //获取user里面的数据
        User user = JSON.parseObject(userString, User.class);
        if(user==null){
            //验证码码失效
            throw new TanHuaException(ErrorResult.error());
        }
        //续签
        redisTemplate.expire(uid+token,1, TimeUnit.DAYS);
        return user;
    }

    /**
     * 头像上传
     * @param headPhoto
     */
    public void saveUserInfoUpdate(MultipartFile headPhoto) {
     try{
         //识别图片
         boolean isbok = faceTemplate.detect(headPhoto.getBytes());
         if(!isbok){
             //图片识别失败
             throw  new TanHuaException("图片识别失败");
         }
         //上传图片
         InputStream inputStream = headPhoto.getInputStream();

         //上传返回文件名称
         String imgName = ossTemplate.upload(headPhoto.getOriginalFilename(), inputStream);

         //封装对象
         UserInfo userInfo = new UserInfo();
         userInfo.setAvatar(imgName);
         userInfo.setId(UserHolder.getUserId());

         userInfoApi.saveUserInfoUpdate(userInfo);
     }catch (Exception e){
         e.printStackTrace();
         throw new TanHuaException(ErrorResult.error());
     }

    }

    /**
     * 查询个人信息
     * @param userId
     * @return
     */
    public UserInfoVo findUserById(Long userId) {
        //根据id查询用户信息
        UserInfo userInfo=  userInfoApi.findUserInfoById(userId);
        if(userInfo==null){
            throw new TanHuaException(ErrorResult.error());
        }
        //封装数据
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtils.copyProperties(userInfo,userInfoVo);
        //判断年龄是否为空
        String age = userInfoVo.getAge();
        if(!StringUtils.isEmpty(userInfoVo.getBirthday())){
            userInfoVo.setAge(String.valueOf(GetAgeUtil.getAge(userInfoVo.getBirthday())));
        }

        return userInfoVo;
    }

    /**
     * 更新个人信息
     * @param userInfoVo
     */
    public void updateById(UserInfoVo userInfoVo) {
        //获取用户id
        Long id = UserHolder.getUserId();
        //创建对象
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userInfoVo,userInfo);
        //设置id
        userInfo.setId(id);
        //设置年龄
        userInfo.setAge(GetAgeUtil.getAge(userInfoVo.getBirthday()));
        //更新
        userInfoApi.saveUserInfoUpdate(userInfo);
    }

    /**
     * 更新头像
     */
    public void updateHeader(MultipartFile headPhoto) {
        try{
            //获取用户id
            Long id = UserHolder.getUserId();
            //根据id获取文件名称
            UserInfo userInfoById = userInfoApi.findUserInfoById(id);
            //原图片
            String avatar = userInfoById.getAvatar();
            //扫描图片
            boolean detect = faceTemplate.detect(headPhoto.getBytes());
            if(!detect){
                throw new TanHuaException("图片识别识别");
            }
            //输出流
            InputStream inputStream = headPhoto.getInputStream();
            //上传图片
            String imageName = ossTemplate.upload(headPhoto.getOriginalFilename(), inputStream);
            //封装对象
            UserInfo info = new UserInfo();
            info.setAvatar(imageName);
            info.setId(id);
            //删除服务器上的照片
            ossTemplate.delete(avatar);
            //更新头像
            userInfoApi.saveUserInfoUpdate(info);

        }catch (Exception e){
            e.printStackTrace();
            throw new TanHuaException(ErrorResult.error());
        }
    }
}
