package com.tanhua.server.controller;

import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.RecommendUserQueryParam;
import com.tanhua.domain.vo.TodayBestVo;
import com.tanhua.server.service.TodayBestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
