package com.hive.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hive.takeout.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
