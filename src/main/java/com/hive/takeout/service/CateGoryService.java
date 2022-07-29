package com.hive.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hive.takeout.entity.Category;

public interface CateGoryService extends IService<Category> {
    public void removeByCategoryId(Long id);
}
