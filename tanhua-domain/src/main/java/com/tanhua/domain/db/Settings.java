package com.tanhua.domain.db;

import lombok.Data;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/6/26 --星期六  下午 10:18
 **/
@Data
public class Settings extends BasePojo {
    private Long id;
    private Long userId;
    private Boolean likeNotification;
    private Boolean pinglunNotification;
    private Boolean gonggaoNotification;
}