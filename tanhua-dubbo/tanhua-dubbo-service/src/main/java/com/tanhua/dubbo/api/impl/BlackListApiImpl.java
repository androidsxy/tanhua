package com.tanhua.dubbo.api.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.BlackList;
import com.tanhua.dubbo.api.BlackListApi;
import com.tanhua.dubbo.mapper.BlackListMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/6/29 --星期二  下午 06:18
 **/
@Service
public class BlackListApiImpl implements BlackListApi {

    @Autowired
    private BlackListMapper blackListMapper;

    @Override
    public IPage<BlackList> findBlackById(Page page1, Long userId) {
        QueryWrapper<BlackList> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        return blackListMapper.selectPage(page1,queryWrapper);
    }

    @Override
    public void delete(int uid, Long userId) {
        QueryWrapper<BlackList> wapper = new QueryWrapper();
        wapper.eq("user_id",userId).eq("black_user_id",uid);
        blackListMapper.delete(wapper);
    }
}
