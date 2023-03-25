package cn.cc.xiaobeiele.dto;

import cn.cc.xiaobeiele.pojo.OrderDetail;
import cn.cc.xiaobeiele.pojo.Orders;


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
