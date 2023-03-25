package cn.cc.xiaobeiele.controller;


import cn.cc.xiaobeiele.common.R;
import cn.cc.xiaobeiele.dto.OrdersDto;
import cn.cc.xiaobeiele.pojo.Orders;
import cn.cc.xiaobeiele.service.OrdersService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    //用户下单
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        ordersService.submit(orders);
        return R.success("下单成功！");
    }

    //后台分页展示订单数据
    @GetMapping("/page")
    public R<Page> pageOrder(int page,int pageSize,Long number,String beginTime,String endTime){
        Page ordersServiceByPage = ordersService.findByPage(page, pageSize, number, beginTime, endTime);
        return R.success(ordersServiceByPage);
    }

    //后台修改订单状态
    @PutMapping
    public R<String> changeOrderStatus(@RequestBody Orders orders){
        ordersService.changeOrderStatus(orders);
        return R.success("修改成功！");
    }

    //前台展示订单数据
    @GetMapping("/userPage")
    public R<Page> showOrderByPage(int page, int pageSize){
        return R.success(ordersService.showOrderByPage(page,pageSize));
    }
}
