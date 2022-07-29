package com.hive.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hive.takeout.common.R;
import com.hive.takeout.entity.Category;
import com.hive.takeout.service.CateGoryService;
import com.hive.takeout.service.DishService;
import com.hive.takeout.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CateGoryService cateGoryService;

    //新增菜品
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("进入到新增菜品");
        cateGoryService.save(category);
        return R.success("新增菜品成功");
    }

    //分页查询
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        log.info("进入到分页查询");
        //构造分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();

        //按照升序进行查询,依据sort字段
        queryWrapper.orderByDesc(Category::getSort);

        //进行分页查询
        cateGoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    //根据Id删除操作
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("删除当前分类"+ids);
        cateGoryService.removeByCategoryId(ids);
        return R.success("分类信息删除成功");
    }

    //修改分类
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("进入到修改分类操作");
        cateGoryService.updateById(category);
        return R.success("修改分类成功");
    }

    //查询菜品分类
    @GetMapping("/list")
    public R<List<Category>> listCategory(Category category){
        log.info("查询菜品分类");
        //构建条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        //添加条件
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());

        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = cateGoryService.list(queryWrapper);
        return R.success(list);
    }
}
