package com.hive.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hive.takeout.common.CustomException;
import com.hive.takeout.common.TreadLocalContent;
import com.hive.takeout.dto.OrdersDto;
import com.hive.takeout.entity.*;
import com.hive.takeout.mapper.OrdersMapper;
import com.hive.takeout.service.AddressBookService;
import com.hive.takeout.service.OrderDetailService;
import com.hive.takeout.service.ShoppingCartService;
import com.hive.takeout.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements com.hive.takeout.service.OrdersService {
    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Transactional
    public void submit(Orders orders) {
       //查询当前用户的id
        Long userId = TreadLocalContent.get();

        //根据当前用户的id查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(userId!=null,ShoppingCart::getUserId,userId);

        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);

        //判断购物车是否为空，为空要抛出异常
        if (shoppingCartList==null){
            throw new CustomException("购物车为空，不能进行付款");
        }

        //将数据封装到orders对象
        Long orderId = IdWorker.getId();

        //创建一个OrderDetail对象的list集合
        List<OrderDetail> detailList = new ArrayList<>();
        shoppingCartList.forEach(item ->{
            //封装orderDetail
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setNumber(item.getNumber());
            orderDetail.setOrderId(orderId);
            if (orders.getAmount()==null){
                System.out.println("为null----------------------------------");
                orders.setAmount(new BigDecimal(0.00));
            }
            BigDecimal amount;
            Integer number1 = item.getNumber();
            Double amount1 = item.getAmount().doubleValue();
            amount = new BigDecimal(number1*amount1);
            orderDetail.setAmount(amount);
            orders.setAmount(orders.getAmount().add(amount));
            detailList.add(orderDetail);
        });
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setOrderTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setUserId(userId);
        orders.setId(orderId);
        orders.setNumber(String.valueOf(orderId));

        //得到手机号
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(userId!=null,User::getId,userId);
        User one = userService.getOne(lambdaQueryWrapper);
        orders.setPhone(one.getPhone());

        //得到当前用户的名称
        orders.setUserName(one.getName());

        //得到地址
        LambdaQueryWrapper<AddressBook> addressBookWrapper = new LambdaQueryWrapper<>();
        addressBookWrapper.eq(orders.getAddressBookId()!=null,AddressBook::getId,orders.getAddressBookId());
        AddressBook addressBookOne = addressBookService.getOne(addressBookWrapper);
        orders.setAddress(addressBookOne.getDetail());
        orders.setConsignee(addressBookOne.getConsignee());

        //将数据插入到orders表
        this.save(orders);

        //将数据插入到orderDetail表
        orderDetailService.saveBatch(detailList);

        //清空购物车
        shoppingCartService.remove(queryWrapper);
    }

    @Override
    public Page<OrdersDto> userPage(int page, int pageSize) {
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrdersDto> ordersDtoPageInfo = new Page<>();

        List<OrdersDto> list = new ArrayList<>();

        //查询订单数据
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        Long userId = TreadLocalContent.get();
        queryWrapper.eq(userId!=null,Orders::getUserId,userId);
        this.page(pageInfo, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,ordersDtoPageInfo,"records");

        List<Orders> records = pageInfo.getRecords();
        records.forEach(item ->{
            OrdersDto ordersDto = new OrdersDto();
            //对象拷贝
            BeanUtils.copyProperties(item,ordersDto);

            ordersDto.setAddress(item.getAddress());
            ordersDto.setUserName(item.getUserName());
            ordersDto.setConsignee(item.getConsignee());
            ordersDto.setPhone(item.getPhone());

            //查询订单明细表数据
            LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(item.getId()!=null,OrderDetail::getOrderId,item.getId());
            List<OrderDetail> orderDetailList = orderDetailService.list(wrapper);
            ordersDto.setOrderDetails(orderDetailList);
            list.add(ordersDto);
        });

        ordersDtoPageInfo.setRecords(list);
        return ordersDtoPageInfo;
    }
}
