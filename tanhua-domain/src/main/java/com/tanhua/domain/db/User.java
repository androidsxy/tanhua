package com.tanhua.domain.db;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/6/24 --星期四  上午 10:21
 **/
@Data
public class User extends BasePojo {
    private Long id;
    private String mobile; //手机号
    private String password; //密码，json序列化时忽略
}
