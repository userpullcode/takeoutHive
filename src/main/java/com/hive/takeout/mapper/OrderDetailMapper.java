package com.hive.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hive.takeout.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
