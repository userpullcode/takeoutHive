package com.hive.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hive.takeout.common.R;
import com.hive.takeout.dto.DishDto;
import com.hive.takeout.entity.Category;
import com.hive.takeout.entity.Dish;
import com.hive.takeout.entity.DishFlavor;
import com.hive.takeout.service.CateGoryService;
import com.hive.takeout.service.DishFlavorService;
import com.hive.takeout.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private CateGoryService cateGoryService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    RedisTemplate<Object,Object> redisTemplate;
    //增加菜品
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("增加菜品");

        //删除对应菜品的redis缓存
        String key = "dish_"+dishDto.getCategoryId()+"status_"+dishDto.getStatus();
        redisTemplate.delete(key);

        dishService.dishAndFlavorSave(dishDto);
        return R.success("添加成功");
    }

    //分页
    @GetMapping("/page")
    public R<Page<DishDto>> page(int page,int pageSize,String name){
        log.info("dish分页==============>"+page+pageSize+name);
        //构造分页控制器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(name!=null,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getCreateTime);

        dishService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = new ArrayList<>();
        records.forEach(item ->{
            DishDto dishDto  = new DishDto();
            //对象封装
            BeanUtils.copyProperties(item,dishDto);
            //得到从dish表中查询出来的categoryId
            Long categoryId = item.getCategoryId();
            //在从category表中查询出对应的名称
            Category category = cateGoryService.getById(categoryId);
            String categoryName = category.getName();
            //将该名称封装到dishDto对象
            dishDto.setCategoryName(categoryName);
            list.add(dishDto);
        });
        //将list对象封装到dishDtoPage
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    //菜品查询
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        log.info("菜品查询");
        DishDto dishDto = dishService.getDishDtoById(id);
        return R.success(dishDto);
    }

    //保存菜品
    @PutMapping
    public R<String> put(@RequestBody DishDto dishDto){

        //删除对应菜品的redis缓存
        String key = "dish_"+dishDto.getCategoryId()+"status_"+dishDto.getStatus();
        redisTemplate.delete(key);

        dishService.put(dishDto);
        return R.success("修改菜品成功");
    }

    //批量删除菜品
    @DeleteMapping
    public R<String> delete(Long[] ids){
        log.info("批量删除菜品");
        dishService.delete(ids);
        return R.success("批量删除菜品成功");
    }

    //菜品批量起售
    @PostMapping("/status/1")
    public R<String> statusOn(String[] ids){
        log.info("菜品批量起售"+ids);
        for (int i = 0; i < ids.length; i++) {
            LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(ids[i]!=null,Dish::getStatus,1);
            updateWrapper.eq(ids[i]!=null,Dish::getId,ids[i]);
            dishService.update(updateWrapper);
        }
        return R.success("菜品批量起售成功");
    }

    //菜品批量停售
    @PostMapping("/status/0")
    public R<String> statusOff(String[] ids){
        log.info("菜品批量起售"+ids);
        for (int i = 0; i < ids.length; i++) {
            LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(ids[i]!=null,Dish::getStatus,0);
            updateWrapper.eq(ids[i]!=null,Dish::getId,ids[i]);
            dishService.update(updateWrapper);
        }
        return R.success("菜品批量停售成功");
    }

    //查询菜品
    @GetMapping("/list")
    public R<List<DishDto>> listDish(Long categoryId,Integer status){

        //动态构造redis的key
        String key = "dish_"+categoryId+"status_"+status;

        //判断缓存中是否有数据,如果有就直接返回
        if ((List<DishDto>) redisTemplate.opsForValue().get(key)!=null){
            return R.success((List<DishDto>) redisTemplate.opsForValue().get(key));
        }

        List<DishDto> dishDtoList = new ArrayList<>();

        //没有在数据库中查询
        log.info("查询菜品"+categoryId);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(categoryId!=null,Dish::getCategoryId,categoryId);
        queryWrapper.eq(status!=null,Dish::getStatus,status);
        List<Dish> list = dishService.list(queryWrapper);
        list.forEach(item ->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(id!=null,DishFlavor::getDishId,id);
            List<DishFlavor> dishFlavors = dishFlavorService.list(wrapper);
            dishDto.setFlavors(dishFlavors);
            dishDtoList.add(dishDto);
        });

        //将查出的数据保存在缓存中
        redisTemplate.opsForValue().set(key,dishDtoList);

        return R.success(dishDtoList);
    }
}
