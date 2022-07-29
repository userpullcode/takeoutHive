package com.hive.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hive.takeout.dto.DishDto;
import com.hive.takeout.entity.Dish;

public interface DishService extends IService<Dish> {
    //将前端添加的菜品保存在dish和dish_flavor表
    public void dishAndFlavorSave(DishDto dishDto);

    //将dish和dish_flavor表中的数据封装到该对象
    DishDto getDishDtoById(Long id);

    //将该对象的值保存在dish和dish_flavor表
    void put(DishDto dishDto);

    //批量删除菜品，并且要把dish_flavor表中数据要清理干净
    void delete(Long[] ids);
}
