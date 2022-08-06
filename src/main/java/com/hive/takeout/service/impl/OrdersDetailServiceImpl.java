package com.hive.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hive.takeout.entity.OrderDetail;
import com.hive.takeout.mapper.OrderDetailMapper;
import com.hive.takeout.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrdersDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
