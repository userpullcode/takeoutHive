package com.hive.takeout.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hive.takeout.dto.SetmealDto;
import com.hive.takeout.entity.Setmeal;

public interface SetMealService extends IService<Setmeal> {
    //添加套餐，将添加套餐的信息分别保存在setmeal表和setmeal_dish表
    void saveMeal(SetmealDto setmealDto);

    //套餐分页
    Page<SetmealDto> setMealPage(int page, int pageSize,String name);

    //保存套餐，分别保存在setmeal表和setmeal_dish表
    void updateSetMealAndDish(SetmealDto setmealDto);

    //删除套餐，分别在setmeal表和setmeal_dish表删除对应的套餐
    void delete(Long[] id);
}
