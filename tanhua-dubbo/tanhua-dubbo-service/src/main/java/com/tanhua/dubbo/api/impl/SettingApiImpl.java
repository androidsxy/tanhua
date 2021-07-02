package com.tanhua.dubbo.api.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tanhua.domain.db.Settings;
import com.tanhua.dubbo.api.SettingApi;
import com.tanhua.dubbo.mapper.SettingMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/6/29 --星期二  下午 04:59
 **/

@Service
public class SettingApiImpl implements SettingApi {

    @Autowired
    private SettingMapper settingMapper;

    @Override
    public Settings findSettingById(Long userId) {
        QueryWrapper<Settings> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        return settingMapper.selectOne(queryWrapper);
    }

    @Override
    public void sevrSetting(Settings setting) {
        settingMapper.insert(setting);
    }

    @Override
    public void updateSetting(Settings setting) {
        settingMapper.updateById(setting);
    }
}
