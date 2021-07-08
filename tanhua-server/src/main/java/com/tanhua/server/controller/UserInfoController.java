package com.tanhua.server.controller;

import com.tanhua.domain.vo.UserInfoVo;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/6/27 --星期日  下午 04:25
 **/
@RestController
@RequestMapping("/users")
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;


    /**
     * 查询数据
     * @param userID
     * @param huanxinID
     * @return
     */
    @GetMapping
    public ResponseEntity getUserInfo(Long userID, Long huanxinID){
        //判断id是否为空
        Long userId = huanxinID;
        if(userId == null ){
            userId = userID;
        }
        if(userId==null){
          userId = UserHolder.getUserId();
        }
        UserInfoVo userInfoVo   =  userInfoService.findUserById(userId);

        return ResponseEntity.ok(userInfoVo);
    }

    /**
     * 更新数据
     * @return
     */
    @PutMapping
    public ResponseEntity updateUserInfo(@RequestBody UserInfoVo userInfoVo){

        //更新数据
        userInfoService.updateById(userInfoVo);
        return ResponseEntity.ok(null);
    }

    /**
     * 更新头像
     * @return
     */
    @RequestMapping(value = "/header",method = RequestMethod.POST)
    public ResponseEntity updateHeader(MultipartFile headPhoto){
        //更新头像
        userInfoService.updateHeader(headPhoto);

        return ResponseEntity.ok(null);
    }


}
