package com.hive.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hive.takeout.common.R;
import com.hive.takeout.dto.SetmealDto;
import com.hive.takeout.entity.DishFlavor;
import com.hive.takeout.entity.Setmeal;
import com.hive.takeout.entity.SetmealDish;
import com.hive.takeout.service.SetMealDishService;
import com.hive.takeout.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetMealController {
    @Autowired
    private SetMealService setMealService;

    @Autowired
    private SetMealDishService setMealDishService;

    //保存套餐
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("保存套餐");
        setMealService.saveMeal(setmealDto);
        return R.success("添加套餐成功");
    }

    //套餐分页
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page,int pageSize,String name){
        log.info("套餐分页");
        Page<SetmealDto> pageInfo = setMealService.setMealPage(page,pageSize,name);
        return R.success(pageInfo);
    }

    //通过id主键得到SetmealDto对象
    @GetMapping("/{id}")
    public R<SetmealDto> getSetMealById(@PathVariable Long id){
        log.info("得到setmealDto对象方法");
        Setmeal setmeal = setMealService.getById(id);
        SetmealDto setmealDto = new SetmealDto();

        //对象拷贝
        BeanUtils.copyProperties(setmeal,setmealDto);

        //创建条件构造器
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SetmealDish::getSetmealId,id);

        //得到dish_flavor表关于该id的数据
        List<SetmealDish> list = setMealDishService.list(queryWrapper);

        //封装到setmealDto对象
        setmealDto.setSetmealDishes(list);
        return R.success(setmealDto);
    }

    //保存修改的套餐
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        log.info("保存修改的套餐");
        setMealService.updateSetMealAndDish(setmealDto);
        return R.success("修改套餐成功");
    }

    //删除（批量）套餐
    @DeleteMapping
    public R<String> delete(Long[] ids){
        log.info("删除（批量）套餐");
        setMealService.delete(ids);
        return R.success("删除成功");
    }

    //套餐（批量）起售
    @PostMapping("/status/1")
    public R<String> statusOn(Long[] ids){
        log.info("套餐（批量）起售");

        for (int i = 0; i < ids.length; i++) {
            //创建条件构造器
            LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(ids[i]!=null,Setmeal::getStatus,"1");
            setMealService.update(updateWrapper);
        }

        return R.success("套餐起售成功");
    }

    //套餐（批量）停售
    @PostMapping("/status/0")
    public R<String> statusOff(Long[] ids){
        log.info("套餐（批量）停售");

        for (int i = 0; i < ids.length; i++) {
            //创建条件构造器
            LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(ids[i]!=null,Setmeal::getStatus,"0");
            setMealService.update(updateWrapper);
        }

        return R.success("套餐停售成功");
    }
}
