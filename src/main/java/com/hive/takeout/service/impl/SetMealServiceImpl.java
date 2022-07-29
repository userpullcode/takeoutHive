package com.hive.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hive.takeout.common.R;
import com.hive.takeout.dto.SetmealDto;
import com.hive.takeout.entity.Category;
import com.hive.takeout.entity.Setmeal;
import com.hive.takeout.entity.SetmealDish;
import com.hive.takeout.mapper.SetMealMapper;
import com.hive.takeout.service.CateGoryService;
import com.hive.takeout.service.SetMealDishService;
import com.hive.takeout.service.SetMealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {
    @Autowired
    private SetMealDishService setMealDishService;

    @Autowired
    private CateGoryService cateGoryService;

    @Transactional
    @Override
    public void saveMeal(SetmealDto setmealDto) {
        //将信息保存在setmeal表
        this.save(setmealDto);

        //将信息保存在setmeal_dish表
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.forEach(item ->{
            item.setSetmealId(setmealDto.getId());
        });
        setMealDishService.saveBatch(setmealDishes);
    }

    @Override
    public Page<SetmealDto> setMealPage(int page, int pageSize,String name) {
        //创建条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(name!=null,Setmeal::getName,name);

        //从setmeal表中拿到分页数据
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        this.page(pageInfo,queryWrapper);

        Page<SetmealDto> setmealDtoPage = new Page<>();
        List<SetmealDto> list = new ArrayList<>();
        //对象拷贝
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");
        //得到pageInfo对象的records属性，找到categoryId对应的分类名称，并进行封装
        List<Setmeal> records = pageInfo.getRecords();
        records.forEach(item ->{
            //拿到对应的category
            Long categoryId = item.getCategoryId();
            Category category = cateGoryService.getById(categoryId);

            //创建一个SetmealDto对象，用于封装record里面的属性
            SetmealDto setmealDto = new SetmealDto();

            //对象拷贝
            BeanUtils.copyProperties(item,setmealDto);
            setmealDto.setCategoryName(category.getName());
            list.add(setmealDto);
        });
        //将list集合封装到setmealDtoPage中
        setmealDtoPage.setRecords(list);
        return setmealDtoPage;
    }

    @Override
    public void updateSetMealAndDish(SetmealDto setmealDto) {
        //保存在setmeal表
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Setmeal::getId,setmealDto.getId());
        this.update(setmealDto,updateWrapper);

        Long setmealId = setmealDto.getId();

        //创建条件构造器
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealId);
        //清除该套餐在数据库setmeal_dish表中的数据
        setMealDishService.remove(queryWrapper);

        //将setmealId封装到setmealDto对象
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.forEach(item ->{
            item.setSetmealId(setmealId);
        });

        //将该套餐新增的数据添加到setmeal_dish表
        setMealDishService.saveBatch(setmealDishes);
    }

    @Override
    public void delete(Long[] ids) {
        //删除setmeal表中对应的数据
        this.removeByIds(Arrays.asList(ids));

        for (int i = 0; i < ids.length; i++) {
            //创建条件构造器
            LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SetmealDish::getSetmealId,ids[i]);
            //删除setmeal_dish表中对应的数据
            setMealDishService.remove(queryWrapper);
        }
    }
}
