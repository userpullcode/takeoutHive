package com.hive.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hive.takeout.dto.DishDto;
import com.hive.takeout.entity.Dish;
import com.hive.takeout.entity.DishFlavor;
import com.hive.takeout.mapper.DishMapper;
import com.hive.takeout.service.DishFlavorService;
import com.hive.takeout.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Transactional
    @Override
    public void dishAndFlavorSave(DishDto dishDto) {
        //保存在dish表中
        this.save(dishDto);

        //得到添加菜品的id
        Long id = dishDto.getId();

        //将该id添加到dishDto对象的flavor中的id
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach(item ->{
            item.setDishId(id);
        });

        //保存在dish_flavor表
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getDishDtoById(Long id) {
        DishDto dishDto = new DishDto();
        //从dish表查出该id对应的记录
        Dish dish = this.getById(id);
        //对象拷贝
        BeanUtils.copyProperties(dish,dishDto);
        //从dish_flavor表中查询该id记录
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id!=null,DishFlavor::getDishId,id);
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(list);
        return dishDto;
    }

    @Override
    public void put(DishDto dishDto) {
        Long dishId = dishDto.getId();
        //清除dish_flavor表中dishId为该id的数据
        LambdaQueryWrapper<DishFlavor> dishFlavorQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorQueryWrapper.eq(dishId!=null,DishFlavor::getDishId,dishId);
        dishFlavorService.remove(dishFlavorQueryWrapper);
        //将表中的数据保存在dish_flavor表
        List<DishFlavor> list = dishDto.getFlavors();
        list.forEach(item ->{
            item.setDishId(dishId);
        });
        dishFlavorService.saveBatch(list);
        //将表中的数据保存在dish表
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(Dish::getId,dishId);
        this.update(dishDto,dishQueryWrapper);
    }

    @Override
    public void delete(Long[] ids) {
        //删除dish表中的数据
        List<Long> list = Arrays.asList(ids);
        this.removeByIds(list);

        //将dishId对应的dish_flavor表中数据删除
        list.forEach(item ->{
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(DishFlavor::getDishId,item);
            dishFlavorService.remove(queryWrapper);
        });
    }
}
