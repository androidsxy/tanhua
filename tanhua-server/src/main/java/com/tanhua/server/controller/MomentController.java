package com.tanhua.server.controller;

import com.tanhua.domain.vo.MomentVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.PublishVo;
import com.tanhua.server.service.MommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/7/3 --星期六  下午 04:15
 **/

@RestController
@RequestMapping("/movements")


/**
 * 圈子功能controller层
 */
public class MomentController {

    @Autowired
    private MommentService mommentService;


    /**
     * 发布动态
     * @param publishVo
     * @param multipartFiles
     * @return
     */
    @PostMapping
    public ResponseEntity movements(PublishVo publishVo, MultipartFile [] multipartFiles){

        mommentService.movements(publishVo,multipartFiles);

        return ResponseEntity.ok(null);
    }


    /**
     * 好友动态
     * @return
     */
    @GetMapping
    public ResponseEntity queryFriendPublishList(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pagesizs){
        page=page<1?1:page;
        PageResult<MomentVo> pageResult = mommentService.queryFriendPublishList(page,pagesizs);
        return ResponseEntity.ok(pageResult);
    }


    /**
     * 推荐动态
     * @param page
     * @param pagesizs
     * @return
     */
    @GetMapping("/recommend")
    public ResponseEntity queryTuiJiaPublishList(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pagesizs){
        page = page<1?1:page;
        PageResult<MomentVo> pageResult = mommentService.queryTuiJiaPublishList(page,pagesizs);
        return ResponseEntity.ok(pageResult);
    }


}
