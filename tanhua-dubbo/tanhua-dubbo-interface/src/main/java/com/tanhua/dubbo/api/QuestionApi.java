package com.tanhua.dubbo.api;

import com.tanhua.domain.db.Question;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/6/29 --星期二  下午 04:58
 **/
public interface QuestionApi {
    Question findQuestionById(Long userId);

    void insert(Question question);

    void updateQuestion(Question question);
}
