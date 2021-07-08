package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.Announcement;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/6/30 --星期三  上午 09:56
 **/
public interface AnnouncementApi {
    IPage<Announcement> findAllannouncements(Page page1);
}
