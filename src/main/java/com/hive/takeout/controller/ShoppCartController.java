package com.hive.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hive.takeout.common.R;
import com.hive.takeout.common.TreadLocalContent;
import com.hive.takeout.entity.Dish;
import com.hive.takeout.entity.ShoppingCart;
import com.hive.takeout.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("添加购物车"+shoppingCart.toString());

        /**判断在购物车中是否有该菜品（套餐）
         * 如果有就在shoppingCart对象的amount属性加1，然后返回对象
         * 如果没有，要添加到shoppingCart表中，在返回对象
         */
        //判断菜品
        if (shoppingCart.getDishId()!=null){
            //表示添加的是菜品
            LambdaQueryWrapper<ShoppingCart> dishWrapper = new LambdaQueryWrapper();
            dishWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
            dishWrapper.eq(ShoppingCart::getUserId,TreadLocalContent.get());
            ShoppingCart one = shoppingCartService.getOne(dishWrapper);
            if (one!=null){
                //表示shoppingCart表中有这个数据
                one.setNumber(one.getNumber()+1);
                LambdaUpdateWrapper<ShoppingCart> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
                updateWrapper.eq(ShoppingCart::getUserId,TreadLocalContent.get());
                updateWrapper.set(ShoppingCart::getNumber,one.getNumber());
                shoppingCartService.update(updateWrapper);
                return R.success(one);
            }else {
                //表示shoppingCart表中没有这个数据，需要添加到shoppingCart表
                shoppingCart.setUserId(TreadLocalContent.get());
                shoppingCart.setCreateTime(LocalDateTime.now());
                shoppingCart.setNumber(1);
                shoppingCartService.save(shoppingCart);
            }
        }

        //判断套餐
        if (shoppingCart.getSetmealId()!=null){
            //表示添加的是菜品
            LambdaQueryWrapper<ShoppingCart> setmealWrapper = new LambdaQueryWrapper();
            setmealWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
            ShoppingCart one = shoppingCartService.getOne(setmealWrapper);
            if (one!=null){
                //表示shoppingCart表中有这个数据
                one.setNumber(one.getNumber()+1);
                LambdaUpdateWrapper<ShoppingCart> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
                updateWrapper.eq(ShoppingCart::getUserId,TreadLocalContent.get());
                updateWrapper.set(ShoppingCart::getNumber,one.getNumber());
                shoppingCartService.update(updateWrapper);
                return R.success(one);
            }else {
                //表示shoppingCart表中没有这个数据，需要添加到shoppingCart表
                shoppingCart.setUserId(TreadLocalContent.get());
                shoppingCart.setCreateTime(LocalDateTime.now());
                shoppingCart.setNumber(1);
                shoppingCartService.save(shoppingCart);
            }
        }
        return R.success(shoppingCart);
    }

    /**
     * 购物车列表
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> listShoppingCart(){
        log.info("购物车列表");

        //条件构造器，根据用户id来进行查询购物车列表
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,TreadLocalContent.get());
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 删减购物车数量
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
       log.info("删减购物车数量");

       ShoppingCart shoppingCart1 = new ShoppingCart();
       //删减菜品
        if (shoppingCart.getDishId()!=null){
            //从数据库中找到该对象
            LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
            queryWrapper.eq(ShoppingCart::getUserId,TreadLocalContent.get());
            ShoppingCart one = shoppingCartService.getOne(queryWrapper);
            LambdaUpdateWrapper<ShoppingCart> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
            updateWrapper.eq(ShoppingCart::getUserId,TreadLocalContent.get());

            //如果数据库中的数据为1，则直接删除在该购物车的菜品
            if (one.getNumber()==1){
                LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
                lambdaQueryWrapper.eq(ShoppingCart::getUserId,TreadLocalContent.get());
                shoppingCartService.remove(lambdaQueryWrapper);
            }else {
                //没有就对数据库中的number字段进行减1
                updateWrapper.set(ShoppingCart::getNumber,one.getNumber()-1);
                shoppingCartService.update(updateWrapper);
            }
            shoppingCart1 = shoppingCartService.getOne(queryWrapper);
        }

        //删减套餐
        if (shoppingCart.getSetmealId()!=null){
            //从数据库中找到该对象
            LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
            queryWrapper.eq(ShoppingCart::getUserId,TreadLocalContent.get());
            ShoppingCart one = shoppingCartService.getOne(queryWrapper);
            LambdaUpdateWrapper<ShoppingCart> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
            updateWrapper.eq(ShoppingCart::getUserId,TreadLocalContent.get());

            //如果数据库中的数据为1，则直接删除在该购物车的菜品
            if (one.getNumber()==1){
                LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
                lambdaQueryWrapper.eq(ShoppingCart::getUserId,TreadLocalContent.get());
                shoppingCartService.remove(lambdaQueryWrapper);
            }else {
                //没有就对数据库中的number字段进行减1
                updateWrapper.set(ShoppingCart::getNumber,one.getNumber()-1);
                shoppingCartService.update(updateWrapper);
            }
            shoppingCart1 = shoppingCartService.getOne(queryWrapper);
            System.out.println("shoppingCart1"+shoppingCart1);
        }
        return R.success(shoppingCart1);
    }

    //清空购物车
    @DeleteMapping("/clean")
    public R<String> delete(){
        log.info("清空购物车");

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,TreadLocalContent.get());
        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }
}
