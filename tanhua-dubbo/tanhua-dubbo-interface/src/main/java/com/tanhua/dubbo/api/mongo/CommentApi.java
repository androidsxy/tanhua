package com.tanhua.dubbo.api.mongo;

import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.vo.PageResult;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/7/4 --星期日  下午 05:35
 **/
public interface CommentApi {
    Long save(Comment comment);

    Long remove(Comment comment);

    PageResult findAllCommentList(int page, int pagesize, String movementId);

    PageResult findAllById(int page, int pageSize, int i, Long userId);
}
