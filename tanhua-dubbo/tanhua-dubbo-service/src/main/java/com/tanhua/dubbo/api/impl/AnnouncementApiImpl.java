package com.tanhua.dubbo.api.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.Announcement;
import com.tanhua.dubbo.api.AnnouncementApi;
import com.tanhua.dubbo.mapper.AnnouncementMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/6/30 --星期三  上午 09:56
 **/
@Service
public class AnnouncementApiImpl implements AnnouncementApi {


    @Autowired
    private AnnouncementMapper announcementMapper;


    @Override
    public IPage<Announcement> findAllannouncements(Page page1) {
        IPage iPage = announcementMapper.selectPage(page1, null);
        return iPage;
    }
}
