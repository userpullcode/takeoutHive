package com.hive.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hive.takeout.common.CustomException;
import com.hive.takeout.entity.Category;
import com.hive.takeout.entity.Dish;
import com.hive.takeout.entity.Setmeal;
import com.hive.takeout.mapper.CateGoryMapper;
import com.hive.takeout.mapper.DishMapper;
import com.hive.takeout.mapper.SetMealMapper;
import com.hive.takeout.service.CateGoryService;
import com.hive.takeout.service.DishService;
import com.hive.takeout.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CateGoryMapper, Category> implements CateGoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetMealService setMealService;

    @Override
    public void removeByCategoryId(Long id) {
        //从数据库中拿到关联菜品的数量
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper();
        dishQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishQueryWrapper);
        //判断当前分类是否关联了菜品，如果关联了菜品就抛出异常，表示删除失败
        if (count1>1){
            //关联了菜品就抛出异常，表示删除失败
            throw new CustomException("关联了菜品删除失败");
        }

        LambdaQueryWrapper<Setmeal> setMealQueryWrapper = new LambdaQueryWrapper();
        setMealQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setMealService.count(setMealQueryWrapper);
        //判断当前分类是否关联了套餐，如果关联了套餐就抛出异常，表示删除失败
        if (count2>1){
            //关联了菜品就抛出异常，表示删除失败
            throw new CustomException("关联了套餐删除失败");
        }

        //说明都没有关联，就可以直接删除
        super.removeById(id);
    }
}
