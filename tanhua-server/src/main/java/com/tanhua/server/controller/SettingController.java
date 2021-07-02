package com.tanhua.server.controller;

import com.tanhua.domain.db.BlackList;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.UserInfoVo;
import com.tanhua.server.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/6/29 --星期二  下午 04:44
 **/
@RestController
@RequestMapping("/users")
public class SettingController {


    @Autowired
    private SettingService settingService;


    /**
     * 通用设置查询
     * @return
     */
    @GetMapping("/settings")
    public ResponseEntity  querySettings(){
        return settingService.querySettings();
    }

    /**
     * 通用设置更新
     * @return
     */
    @PostMapping("/notifications/setting")
    public ResponseEntity notifications(@RequestBody Map<String,Boolean> map){
        //获取用户数据
        Boolean likeNotification = map.get("likeNotification");
        Boolean pinglunNotification = map.get("pinglunNotification");
        Boolean gonggaoNotification = map.get("gonggaoNotification");
        //更新设置
        settingService.update(likeNotification,pinglunNotification,gonggaoNotification);

        return ResponseEntity.ok(null);
    }

    /**
     * 陌生人问题保存
     * @param map
     * @return
     */
    @PostMapping("/questions")
    public ResponseEntity questions(@RequestBody Map<String,String> map){
        //获取用户数据
        String content = map.get("content");
        settingService.updateQuestions(content);
        return ResponseEntity.ok(null);
    }

    /**
     * 黑名单管理
     */
    @GetMapping("/blacklist")
    public ResponseEntity blacklist(@RequestParam(defaultValue =" 1") int page,
                                    @RequestParam(defaultValue = "10") int pagesize){
        PageResult<UserInfo> pageResult = settingService.blackList(page,pagesize);

        return ResponseEntity.ok(pageResult);
    }

    /**
     * 移除黑名单
     * @param uid
     * @return
     */
    @DeleteMapping("blacklist/{uid}")
    public ResponseEntity blacklistDelete(@PathVariable int uid){
        settingService.blackListDelete(uid);
        return ResponseEntity.ok(null);
    }

    /**
     * 更换手机号   发送验证码
     * @return
     */
    @PostMapping("/phone/sendVerificationCode")
    public ResponseEntity sendVerificationCode(){
        //发送验证码
        settingService.sendVerificationCode();
        return ResponseEntity.ok(null);
    }


    /**
     *校验验证码
     * @param verificationCode
     * @return
     */
    @PostMapping("phone/checkVerificationCode")
    public ResponseEntity checkVerificationCode(@RequestBody  Map<String,String> verificationCode){
        String validateCodeError = verificationCode.get("verificationCode");
        Map map  = settingService.checkVerificationCode(validateCodeError);
        return ResponseEntity.ok(map);
    }


    /**
     * 更换手机好
     * @param map
     * @return
     */
    @PostMapping("/phone")
    public ResponseEntity phoneUpdate(@RequestBody Map map){
        //获取前台数据
        String phone = (String) map.get("phone");
        settingService.phoneUpdate(phone);
        return ResponseEntity.ok(null);
    }
}
