package cn.cc.xiaobeiele.service;

import cn.cc.xiaobeiele.dto.OrdersDto;
import cn.cc.xiaobeiele.pojo.Orders;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpSession;

public interface OrdersService extends IService<Orders> {

    //用户下单
    void submit(Orders orders);

    //后台分页展示订单数据
    Page findByPage(int page,int pageSize,Long number,String beginTime,String endTime);

    //后台修改订单状态
    void changeOrderStatus(Orders orders);

    //前台展示订单数据
    Page showOrderByPage(int page, int pageSize);
}
