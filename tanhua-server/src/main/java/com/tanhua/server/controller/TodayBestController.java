package com.tanhua.server.controller;

import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.RecommendUserQueryParam;
import com.tanhua.domain.vo.TodayBestVo;
import com.tanhua.server.service.IMService;
import com.tanhua.server.service.TodayBestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/7/1 --星期四  下午 06:38
 **/

/**
 * 交友模块
 */
@RestController
@RequestMapping("/tanhua")
public class TodayBestController {

    @Autowired
    private TodayBestService todayBestService;

    @Autowired
    private IMService imService;

    /**
     * 今日佳人
     * @return
     */
    @GetMapping("/todayBest")
    public ResponseEntity findtodayBest(){

        TodayBestVo todayBestVo = todayBestService.findtodayBest();

        return ResponseEntity.ok(todayBestVo);
    }

    /**
     * 推荐朋友
     * @param queryParam
     * @return
     */
    @GetMapping("/recommendation")
    public ResponseEntity recommendation(RecommendUserQueryParam queryParam){

        PageResult<TodayBestVo> pageRequest = todayBestService.recommendation(queryParam);

         return   ResponseEntity.ok(pageRequest);
    }

    /**
     * 佳人信息
     * @param userId
     * @return
     */
    @GetMapping("/{id}/personalInfo")
    public ResponseEntity<TodayBestVo> queryUserDetail(@PathVariable("id") Long userId){
        TodayBestVo userInfoVo = todayBestService.getUserInfo(userId);
        return ResponseEntity.ok(userInfoVo);
    }


    /**
     * 查询陌生人问题
     *
     * @param userId
     * @return
     */
    @GetMapping("/strangerQuestions")
    public ResponseEntity strangerQuestions(@RequestParam Long  userId){

        return ResponseEntity.ok(todayBestService.findByIdQuestion(userId));
    }

    /**
     * 回复陌生人问题
     * @param map
     * @return
     */
    @PostMapping("strangerQuestions")
    public ResponseEntity strangerQuestionsTow(@RequestBody Map<String,Object> map){
        //获取用户信息
        long userId = Long.parseLong(map.get("userId").toString());
        String reply = (String) map.get("reply");
        imService.replyStrangerQuestions(userId,reply);
        return ResponseEntity.ok(null);
    }
}
