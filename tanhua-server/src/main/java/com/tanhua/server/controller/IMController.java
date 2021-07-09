package com.tanhua.server.controller;

import com.tanhua.domain.vo.ContactVo;
import com.tanhua.domain.vo.MessageVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.server.service.IMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/7/9 --星期五  下午 07:15
 **/


@RestController
@RequestMapping("/messages")
public class IMController {

    @Autowired
    private IMService imService;


    /**
     * 添加联系人
     * @param map
     * @return
     */
    @PostMapping("/contacts")
    public ResponseEntity addUser(@RequestBody Map<String,Integer> map){
        //获取前端传递数据
        Long userId = Long.parseLong(map.get("userId").toString());
        imService.addUser(userId);
        return ResponseEntity.ok(null);
    }

    /**
     * 联系人列表
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/contacts")
    public ResponseEntity findFriend(@RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int pageSize){
        PageResult<ContactVo> pageResult = imService.queryContactsList(page,pageSize);

        return ResponseEntity.ok(pageResult);
    }

    /**
     * 喜欢列表
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/loves")
    public ResponseEntity loves(@RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "10") int pageSize){
        PageResult<MessageVo> pageResult = imService.counts(page,pageSize,3);

        return ResponseEntity.ok(pageResult);
    }

    /**
     * 点赞列表
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/likes")
    public ResponseEntity likes(@RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "10") int pageSize){
        PageResult<MessageVo> pageResult = imService.counts(page,pageSize,1);

        return ResponseEntity.ok(pageResult);
    }

    /**
     * 评论列表
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/comments")
    public ResponseEntity comments(@RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "10") int pageSize){
        PageResult<MessageVo> pageResult = imService.counts(page,pageSize,2);

        return ResponseEntity.ok(pageResult);
    }

}
