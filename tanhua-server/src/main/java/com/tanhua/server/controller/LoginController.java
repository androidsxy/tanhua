package com.tanhua.server.controller;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/6/24 --星期四  下午 02:05
 **/

import com.tanhua.domain.vo.UserInfoVo;
import com.tanhua.server.service.UserInfoService;
import com.tanhua.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 服务控制层
 */
@RestController
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * 根据手机号查询用户
     */
    @RequestMapping(value = "/findUser",method = RequestMethod.GET)
    public ResponseEntity findUser(String mobile){
        return userService.findByMobile(mobile);
    }


    /**
     * 新增用户
     */
    @RequestMapping(value = "/saveUser",method = RequestMethod.POST)
    public ResponseEntity saveUser(@RequestBody Map<String,Object> param){
        String mobile = (String)param.get("mobile");
        String password = (String)param.get("password");
        return userService.saveUser(mobile,password);
    }

    /**
     * 发送验证码
     * @param phone
     * @return
     */
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ResponseEntity login(@RequestBody Map phone){
        //获取前台页面传递过来的手机号码
        String phone1 = (String) phone.get("phone");
        //调用业务层发送验证码
        userService.login(phone1);

        return ResponseEntity.ok(null);
    }

    /**
     * 校验验证码
     * @param param
     * @return
     */
    @RequestMapping(value = "/loginVerification",method = RequestMethod.POST)
    public ResponseEntity loginVerification(@RequestBody Map<String,String> param){
        //获取输入的手机号码
        String phone = param.get("phone");
        //获取输入的验证码
        String verificationCode = param.get("verificationCode");
        //创建返回值map对象
        Map<String,Object> map = userService.loginVerification(phone,verificationCode);

        return ResponseEntity.ok(map);
    }
    @Autowired
    private UserInfoService userInfoService;


    /**
     * 完善个人信息
     * @param userInfoVo
     * @return
     */
    @RequestMapping(value = "/loginReginfo",method = RequestMethod.POST)
    public ResponseEntity loginReginfo(@RequestBody UserInfoVo userInfoVo){

        userInfoService.saveUserInfo(userInfoVo);

        return ResponseEntity.ok(null);
    }


    /**
     * 选取头像
     * @return
     */
    @RequestMapping(value = "loginReginfo/head",method = RequestMethod.POST)
    public ResponseEntity loginReginfoHead(MultipartFile headPhoto){

        userInfoService.saveUserInfoUpdate(headPhoto);

        return ResponseEntity.ok(null);
    }

}
