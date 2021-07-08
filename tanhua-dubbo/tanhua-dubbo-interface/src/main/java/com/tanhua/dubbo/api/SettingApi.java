package com.tanhua.dubbo.api;

import com.tanhua.domain.db.Settings;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/6/29 --星期二  下午 04:58
 **/
public interface SettingApi {
    Settings findSettingById(Long userId);

    void sevrSetting(Settings setting);

    void updateSetting(Settings setting);
}
