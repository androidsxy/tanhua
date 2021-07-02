package com.tanhua.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.Announcement;
import com.tanhua.domain.vo.AnnouncementVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.AnnouncementApi;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/6/30 --星期三  上午 09:28
 **/

@Service
public class AnnouncementService {


    @Reference
    private AnnouncementApi announcementApi;

    /**
     * 公告通告分页查询
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult<AnnouncementVo> findAllannouncements(int page, int pagesize) {
        //封装数据
        List<AnnouncementVo> list = new ArrayList<>();
        AnnouncementVo announcementVo;
        //创建page
        Page page1 = new Page(page,pagesize);
        //分页查询公告
        IPage<Announcement> iPage = announcementApi.findAllannouncements(page1);
        if(iPage!=null){
            //重新封装数据
            List<Announcement> records = iPage.getRecords();
            if(records!=null){
                for (Announcement record : records) {
                    announcementVo = new AnnouncementVo();
                    //复制对象
                    BeanUtils.copyProperties(record,announcementVo);
                    announcementVo.setCreateDate(getDate(record.getCreated()));
                    list.add(announcementVo);
                }
            }
        }
        return new PageResult<>(iPage.getCurrent(),Integer.toUnsignedLong(pagesize),iPage.getPages(),Integer.toUnsignedLong(page),list);
    }

    /**
     * 时间转换
     * @param date
     * @return
     */
    private String getDate(Date date){
      return new SimpleDateFormat("yyyy-MM-dd hh:mm").format(date);
    }
}
