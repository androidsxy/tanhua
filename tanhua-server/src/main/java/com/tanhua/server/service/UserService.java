package com.tanhua.server.service;

import com.alibaba.fastjson.JSON;
import com.tanha.commons.exception.TanHuaException;
import com.tanha.commons.templates.HuanXinTemplate;
import com.tanha.commons.templates.SmsTemplate;
import com.tanhua.domain.db.User;
import com.tanhua.domain.vo.ErrorResult;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.server.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/6/24 --星期四  下午 02:08
 **/
@Slf4j
@Service
public class UserService {
    @Reference
    private UserApi userApi;

    @Value("${tanhua.uid}")
    private String uid;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private SmsTemplate  smsTemplate;


    @Autowired
    private HuanXinTemplate huanXinTemplate;

    //注入数据
    @Value("${tanhua.redisValidateCodeKeyPrefix}")
    public String redisValidateCodeKeyPrefix;

    /**
     * 根据手机号查询用户
     * @param mobile
     * @return
     */
    public ResponseEntity findByMobile(String mobile){
        User user = userApi.findByMobile(mobile);
        return ResponseEntity.ok(user);
    }


    /**
     * 添加用户
     * @param mobile
     * @param password
     * @return
     */
    public ResponseEntity saveUser(String mobile,String password){
        User user = new User();
        user.setMobile(password);
        user.setPassword(password);
        userApi.save(user);
        return ResponseEntity.ok(null);
    }

    /**
     * 发送短信
     * @param phone1
     */
    public void login(String phone1) {
        //查看redis里有没有保存验证码
        String code = redisTemplate.opsForValue().get(redisValidateCodeKeyPrefix + phone1);
        //是否为空
        if(!StringUtils.isEmpty(code)){
            //不为空  证明之前发的验证码还未失效
            throw  new TanHuaException(ErrorResult.duplicate());
        }
        //发送验证码
        String code1 = "123456";
        if(false){
            //发送短信
            Map<String, String> map = smsTemplate.sendValidateCode(phone1, code1);
            if(map!=null){
                throw new TanHuaException(ErrorResult.fail());
            }
        }
        log.debug("发送验证码：{}，{}",phone1,code1);
        //发送成功 保存到redis
        redisTemplate.opsForValue().set(redisValidateCodeKeyPrefix+phone1,code1,1, TimeUnit.MINUTES);

    }

    /**
     * 验证码输入校验
     * @param phone
     * @param verificationCode
     * @return
     */
    public Map<String, Object> loginVerification(String phone, String verificationCode) {
        //创建返回对象
        Map<String,Object> map = new HashMap<>();
        //默认已存在
        map.put("isNew",false);
        //查询redis里面保存的验证码
        String code = redisTemplate.opsForValue().get(redisValidateCodeKeyPrefix + phone);
        //是否为空
        if(StringUtils.isEmpty(code)){
            //验证码已失效
            throw new TanHuaException(ErrorResult.loginError());
        }
        if(!code.equals(verificationCode)){
            //验证码输入错误
            throw new TanHuaException(ErrorResult.validateCodeError());
        }
        //查询是否存在该用户
        User user = userApi.findByMobile(phone);
        //判断
        if(user==null){
            user = new User();
            user.setMobile(phone);
            user.setPassword(DigestUtils.md5Hex(phone.substring(phone.length()-6)));
            user.setCreated(new Date());
            user.setUpdated(new Date());
            //返回id存入redis
            Long userID = userApi.save(user);
            user.setId(userID);
            map.put("isNew",true);
            //注册
            huanXinTemplate.register(userID);
        }
        //生成token
        String token = jwtUtils.createJWT(phone, user.getId());
        //把数据用户信息存入redis
        redisTemplate.opsForValue().set(uid+token, JSON.toJSONString(user),1,TimeUnit.DAYS);
        //把token返回页面
        map.put("token",token);
        return map;
    }
}
