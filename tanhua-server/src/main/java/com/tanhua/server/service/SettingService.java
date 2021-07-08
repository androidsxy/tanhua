package com.tanhua.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanha.commons.exception.TanHuaException;
import com.tanha.commons.templates.FaceTemplate;
import com.tanha.commons.templates.SmsTemplate;
import com.tanhua.domain.db.*;
import com.tanhua.domain.vo.ErrorResult;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.SettingsVo;
import com.tanhua.domain.vo.UserInfoVo;
import com.tanhua.dubbo.api.*;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/6/29 --星期二  下午 04:48
 **/

@Service
public class SettingService {

    @Reference
    private SettingApi settingApi;

    @Reference
    private QuestionApi questionApi;


    @Reference
    private BlackListApi blackListApi;

    @Reference
    private UserInfoApi userInfoApi;

    @Autowired
    private SmsTemplate smsTemplate;

    @Autowired
    private UserService userService;

    @Reference
    private UserApi userApi;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    public ResponseEntity querySettings() {
        //获取用户id
        Long userId = UserHolder.getUserId();
        //查询通用设置
        Settings settings =  settingApi.findSettingById(userId);
        //判断settings是否为空
        if(settings==null){
            settings = new Settings();
            //设置默认值
            settings.setPinglunNotification(false);
            settings.setLikeNotification(false);
            settings.setGonggaoNotification(false);
        }
        SettingsVo settingsVo = new SettingsVo();
        //复制对象
        BeanUtils.copyProperties(settings,settingsVo);
        //查询问题
        Question  question =  questionApi.findQuestionById(userId);
        //是否存在问题
        if(question==null){
            question = new Question();
            question.setTxt("你是？");
        }
        settingsVo.setStrangerQuestion(question.getTxt());
        settingsVo.setPhone(UserHolder.getUser().getMobile());
        return ResponseEntity.ok(settingsVo);
    }

    /**
     * 更新通用设置
     * @param likeNotification
     * @param pinglunNotification
     * @param gonggaoNotification
     */
    public void update(Boolean likeNotification, Boolean pinglunNotification, Boolean gonggaoNotification) {
        //获取用户id
        Long userId = UserHolder.getUserId();
        //更新之前查询是否存在设置
        Settings setting = settingApi.findSettingById(userId);
        if(setting==null){
            setting = new Settings();
            setting.setUserId(userId);
            setting.setGonggaoNotification(gonggaoNotification);
            setting.setLikeNotification(likeNotification);
            setting.setPinglunNotification(pinglunNotification);
            //添加
            settingApi.sevrSetting(setting);
        }else{
            setting.setGonggaoNotification(gonggaoNotification);
            setting.setLikeNotification(likeNotification);
            setting.setPinglunNotification(pinglunNotification);
            settingApi.updateSetting(setting);
        }
    }

    /**
     * 陌生问题更改
     * @param content
     */
    public void updateQuestions(String content) {
        //当前用户id
        Long userId = UserHolder.getUserId();
        //获取当前问题
        Question question = questionApi.findQuestionById(userId);
        //判断是否为空 如果为空的化插入
        if(question==null){
            //插入
            question = new Question();
            question.setUserId(userId);
            question.setTxt(content);
            questionApi.insert(question);
        }else{
            //更新
            question.setTxt(content);
            questionApi.updateQuestion(question);
        }

    }

    /**
     * 分页查询黑名单
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult<UserInfo> blackList(int page, int pagesize) {
        //获取当前用户id
        Long userId = UserHolder.getUserId();
        //查询黑名单当前用户黑名单的分页查询
        Page page1 = new Page(page,pagesize);

        IPage<BlackList> blackListIPage =  blackListApi.findBlackById(page1,userId);
        //通过黑名单用户id查询用户信息
        UserInfoVo userInfoVo;
        //list<UserInfoVo>
        List<UserInfo> list = new ArrayList<>();
        if(blackListIPage!=null){
            for (BlackList record : blackListIPage.getRecords()) {
                //查询用户信息
                Long blackUserId = record.getBlackUserId();
                UserInfo userInfo = userInfoApi.findUserInfoById(blackUserId);
                list.add(userInfo);
            }
        }


        return new PageResult<UserInfo>(blackListIPage.getCurrent(),Integer.toUnsignedLong(pagesize),blackListIPage.getPages(),Integer.toUnsignedLong(page),list);
    }

    /**
     * 移除黑名单
     * @param uid
     */
    public void blackListDelete(int uid) {
        //获取用户id
        Long userId = UserHolder.getUserId();
        //移除黑名单
        blackListApi.delete(uid,userId);

    }

    /**
     * 更换手机好  发送验证码
     */
    public void sendVerificationCode() {
        //获取当前用户手机号
        String mobile = UserHolder.getUser().getMobile();
        //查询redis里是否存在数据
        String vode = redisTemplate.opsForValue().get(userService.redisValidateCodeKeyPrefix + mobile);
        if(!StringUtils.isEmpty(vode)){
            //上一次验证码还未失效
            throw new TanHuaException(ErrorResult.duplicate());
        }
        //验证码
        String code = "123456";
        //发送验证码
        if(false){
            Map<String, String> map = smsTemplate.sendValidateCode(mobile, code);
            if(map!=null){
                throw new TanHuaException(ErrorResult.fail());
            }
        }
        //保存验证码到redis
        redisTemplate.opsForValue().set(userService.redisValidateCodeKeyPrefix+mobile,code,2,TimeUnit.MINUTES);
    }

    /**
     * 校验验证码
     * @param verificationCode
     * @return
     */
    public Map<String, Object> checkVerificationCode(String verificationCode) {
        //获取当前用户手机号
        String mobile = UserHolder.getUser().getMobile();
        //查询redis里是否存在数据
        String code = redisTemplate.opsForValue().get(userService.redisValidateCodeKeyPrefix + mobile);
        //判断
        if(StringUtils.isEmpty(code)){
            throw  new TanHuaException(ErrorResult.loginError());
        }
        if(!code.equals(verificationCode)){
            throw  new TanHuaException(ErrorResult.validateCodeError());
        }
        Map<String,Object> map = new HashMap<>();
        map.put("verification",true);
        return map;
    }

    /**
     * 更改手机号
     * @param phone
     */
    public void phoneUpdate(String phone) {
        //获取当前用户数据
        String mobile = UserHolder.getUser().getMobile();
        //通过查询单曲phone是否已绑定账号
        User user = userApi.findByMobile(phone);
        //是否已注册
        if(user!=null){
            throw new TanHuaException("该手机号已注册，请重新输入手机号！");
        }
        user = new User();
        user.setMobile(phone);
        user.setId(UserHolder.getUserId());
        //没有更新用户
        userApi.update(user);
        //删除token从新登录
        redisTemplate.delete(userService.redisValidateCodeKeyPrefix+mobile);
    }
}
