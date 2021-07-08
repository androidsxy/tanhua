package com.tanhua.server.controller;

import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.VideoVo;
import com.tanhua.server.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/7/8 --星期四  下午 04:30
 **/


/**
 * 小视频控制层
 */
@RestController
@RequestMapping("/smallVideos")
public class VideoController {

    @Autowired
    public VideoService videoService;

    /**
     * 发布小视频
     * @param videoThumbnail
     * @param videoFile
     * @return
     */
    @PostMapping
    public ResponseEntity saveVideoService(MultipartFile videoThumbnail, MultipartFile videoFile){

        videoService.saveVideoService(videoFile,videoThumbnail);

        return ResponseEntity.ok(null);
    }


    /**
     * 小视频查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping
    public ResponseEntity findAllVideo(@RequestParam(defaultValue = "1") int page,@RequestParam(defaultValue = "10") int pageSize){
     page = page<=0?1:page+1;
     PageResult<VideoVo> pageResult = videoService.findAllVideo(page,pageSize);
     return ResponseEntity.ok(pageResult);
    }


    /**
     * 关注用户
     * @param userId
     * @return
     */
    @PostMapping("/{uid}/userFocus")
    public ResponseEntity userFocus(@PathVariable("uid") String userId){
        videoService.userFocus(userId);
        return ResponseEntity.ok(null);
    }


    /**
     * 取消关注
     * @param userId
     * @return
     */
    @PostMapping("/{uid}/userUnFocus")
    public ResponseEntity userUnFocus(@PathVariable("uid") String userId){
        videoService.userUnFocus(userId);
        return ResponseEntity.ok(null);
    }


}
