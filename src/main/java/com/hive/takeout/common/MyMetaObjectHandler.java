package com.hive.takeout.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("MyMetaObjectHandler的insert");

        //得到session中的id
        Long employeeId = TreadLocalContent.get();

        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", employeeId);
        metaObject.setValue("updateUser", employeeId);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("MyMetaObjectHandler的update");

        //得到session中的id
        Long employeeId = TreadLocalContent.get();

        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", employeeId);
    }
    }
