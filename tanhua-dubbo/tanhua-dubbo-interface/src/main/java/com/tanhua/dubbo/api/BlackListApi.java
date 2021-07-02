package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.BlackList;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/6/29 --星期二  下午 06:17
 **/
public interface BlackListApi {
    IPage<BlackList> findBlackById(Page page1, Long userId);

    void delete(int uid, Long userId);

}
