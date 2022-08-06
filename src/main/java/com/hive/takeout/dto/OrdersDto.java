package com.hive.takeout.dto;

import com.hive.takeout.entity.OrderDetail;
import com.hive.takeout.entity.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
