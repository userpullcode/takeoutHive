package com.hive.takeout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hive.takeout.common.R;
import com.hive.takeout.dto.OrdersDto;
import com.hive.takeout.entity.Orders;
import com.hive.takeout.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    /**
     * 用户提交订单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("用户提交订单");

        ordersService.submit(orders);
        return R.success("用户提交订单成功");
    }

    //展示订单
    @GetMapping("/userPage")
    public R<Page<OrdersDto>> userPage(int page, int pageSize){
        log.info("展示订单");

        Page<OrdersDto> ordersDtoList = ordersService.userPage(page,pageSize);
        return R.success(ordersDtoList);
    }
}
