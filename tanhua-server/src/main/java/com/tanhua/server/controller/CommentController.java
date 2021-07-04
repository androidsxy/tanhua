package com.tanhua.server.controller;

import com.tanhua.domain.vo.CommentVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.server.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/7/4 --星期日  下午 08:14
 **/
@RestController
@RequestMapping("/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;


    /**
     * 评论列表
     * @param movementId
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping
    public ResponseEntity findAllCommentList(String movementId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pagesize) {

        PageResult<CommentVo> pageRequest = commentService.findAllCommentList(movementId,page,pagesize);

        return ResponseEntity.ok(pageRequest);
    }

    /**
     * 发表评论
     * @param paramMap
     * @return
     */
    @PostMapping
    public ResponseEntity add(@RequestBody Map<String,String> paramMap){
        commentService.add(paramMap);
        return ResponseEntity.ok(null);
    }

    /**
     * 评论点赞
     * @param commentId
     * @return
     */
    @GetMapping("/{id}/like")
    public ResponseEntity like(@PathVariable("id") String commentId){
        Long total = commentService.likeComment(commentId);
        return ResponseEntity.ok(total);
    }

    /**
     * 评论取消
     * @param commentId
     * @return
     */
    @GetMapping("/{id}/dislike")
    public ResponseEntity<Long> unlike(@PathVariable("id") String commentId){
        Long total = commentService.unlikeComment(commentId);
        return ResponseEntity.ok(total);
    }

}
