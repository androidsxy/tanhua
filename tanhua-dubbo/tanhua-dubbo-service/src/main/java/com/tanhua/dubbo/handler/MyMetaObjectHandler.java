package com.tanhua.dubbo.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Author ShuXinYuan  舒新元
 * @Date 2021/6/25 --星期五  上午 09:54
 **/
//自动填充类
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 创建时添加
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        //创建时
        setFieldValByName("created",new Date(),metaObject);
        setFieldValByName("updated",new Date(),metaObject);
    }

    /**
     * 更新时添加
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        setFieldValByName("updated",new Date(),metaObject);
    }
}
