package com.hive.takeout.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hive.takeout.dto.OrdersDto;
import com.hive.takeout.entity.Orders;

import java.util.List;

public interface OrdersService extends IService<Orders> {
    //用户提交订单
    void submit(Orders orders);

    //用户订单
    Page<OrdersDto> userPage(int page, int pageSize);
}
