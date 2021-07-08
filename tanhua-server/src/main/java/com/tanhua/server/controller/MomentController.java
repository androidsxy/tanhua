package com.tanhua.server.controller;

import com.tanhua.domain.vo.MomentVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.PublishVo;
import com.tanhua.server.service.CommentService;
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


    @Autowired
    private CommentService commentService;


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


    /**
     * 查询我的相册，即我的动态
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping("/all")
    public ResponseEntity queryMyAlbum(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int pagesize,Long userId){
        page=page<1?1:page;
        PageResult<MomentVo> pageResult = mommentService.queryMyAlbum(page,pagesize,userId);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 动态点赞
     * @param publishId
     * @return
     */
    @GetMapping("/{id}/like")
    public ResponseEntity like(@PathVariable("id") String publishId){
        Long total = commentService.like(publishId);
        return ResponseEntity.ok(total);
    }

    /**
     * 取消点赞
     * @param publishId
     * @return
     */
    @GetMapping("/{id}/dislike")
    public ResponseEntity dislike(@PathVariable("id") String publishId){
        Long total = commentService.dislike(publishId);
        return ResponseEntity.ok(total);
    }
    /**
     * 喜欢
     * @param publishId
     * @return
     */
    @GetMapping("/{id}/love")
    public ResponseEntity<Long> love(@PathVariable("id") String publishId){
        Long total = commentService.love(publishId);
        return ResponseEntity.ok(total);
    }

    /**
     * 取消喜欢
     * @param publishId
     * @return
     */
    @GetMapping("/{id}/unlove")
    public ResponseEntity<Long> unlove(@PathVariable("id") String publishId){
        Long total = commentService.unlove(publishId);
        return ResponseEntity.ok(total);
    }

    /**
     * 单个动态
     * @param publishId
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable("id") String publishId){
       MomentVo momentVo =mommentService.findById(publishId);
        return ResponseEntity.ok(momentVo);
    }

}
