package com.hive.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hive.takeout.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
