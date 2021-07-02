package com.tanhua.server.controller;

import com.tanhua.domain.vo.AnnouncementVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.server.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/6/30 --星期三  上午 09:21
 **/

/**
 * 公告管理
 */


@RestController
@RequestMapping("/messages")
public class AnnouncementController {


    @Autowired
    private AnnouncementService announcementService;

    @GetMapping("/announcements")
    public ResponseEntity announcements(@RequestParam(defaultValue = "1") int page,@RequestParam(defaultValue = "10") int pagesize){

        //查询分页
        PageResult<AnnouncementVo> pageResult = announcementService.findAllannouncements(page,pagesize);
        return ResponseEntity.ok(pageResult);
    }


}
