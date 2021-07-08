package com.tanhua.dubbo.api.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.domain.db.Question;
import com.tanhua.dubbo.api.QuestionApi;
import com.tanhua.dubbo.mapper.QuestionMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/6/29 --星期二  下午 05:00
 **/
@Service
public class QuestionApiImpl implements QuestionApi {

    @Autowired
    private QuestionMapper questionMapper;


    @Override
    public Question findQuestionById(Long userId) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        return questionMapper.selectOne(queryWrapper);
    }

    @Override
    public void insert(Question question) {
        questionMapper.insert(question);
    }

    @Override
    public void updateQuestion(Question question) {
        questionMapper.updateById(question);
    }
}
