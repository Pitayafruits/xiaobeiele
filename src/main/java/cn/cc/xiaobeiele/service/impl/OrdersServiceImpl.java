package cn.cc.xiaobeiele.service.impl;

import cn.cc.xiaobeiele.common.CustomException;
import cn.cc.xiaobeiele.dto.OrdersDto;
import cn.cc.xiaobeiele.mapper.OrdersMapper;
import cn.cc.xiaobeiele.pojo.*;
import cn.cc.xiaobeiele.service.*;
import cn.cc.xiaobeiele.utils.BaseContext;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper,Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    //用户下单
    @Override
    @Transactional
    public void submit(Orders orders) {
        //1.获得登录用户的id
        Long userId = BaseContext.getCurrentId();
        //2.获得当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        if (shoppingCartList == null || shoppingCartList.size() == 0){
            throw new CustomException("订单为空！");
        }
          //2.1 查询用户数据
          User user = userService.getById(userId);
          //2.2 查询地址数据
          Long addressBookId = orders.getAddressBookId();
          AddressBook addressBook = addressBookService.getById(addressBookId);
          if (addressBook == null){
              throw new CustomException("订单地址有误！");
          }
        //3.向订单表中插入数据，一条数据
        //3.1 获取初始变量
        long orderId = IdWorker.getId(); //订单号
        AtomicInteger amount = new AtomicInteger(0); //原子整型金额
        //3.2 完成购物车金额累加 - 订单明细数据的获取
        List<OrderDetail> orderDetails = shoppingCartList.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId); //订单id
            orderDetail.setNumber(item.getNumber()); //数量
            orderDetail.setDishFlavor(item.getDishFlavor()); //口味
            orderDetail.setDishId(item.getDishId()); //菜品ID
            orderDetail.setSetmealId(item.getSetmealId());  //套餐ID
            orderDetail.setName(item.getName()); //名称
            orderDetail.setImage(item.getImage()); //图片
            orderDetail.setAmount(item.getAmount()); //金额
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());
        //
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now()); //下单时间
        orders.setCheckoutTime(LocalDateTime.now()); //结账时间
        orders.setStatus(2); //订单状态->待派送
        orders.setAmount(new BigDecimal(amount.get())); //实收金额
        orders.setUserId(userId); //下单用户id
        orders.setNumber(String.valueOf(orderId)); //订单号
        orders.setUserName(user.getName()); //用户名
        orders.setConsignee(addressBook.getConsignee()); //收货人
        orders.setPhone(addressBook.getPhone()); //手机号
        //地址
        //省级名称+市级名称+区级名称+详细地址
        orders.setAddress((addressBook.getProvinceName() == null ? " " : addressBook.getProvinceName())
        + (addressBook.getCityName() == null ? " " : addressBook.getCityName())
        + (addressBook.getDistrictName() == null ? " " : addressBook.getDistrictName())
        + (addressBook.getDetail() == null ? " " : addressBook.getDetail()));
        //3.4 插入操作
        this.save(orders);
        //4.向订单明细表中插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);
        //5.支付完成后，清空购物车
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
    }

    @Override
    //后台分页展示订单数据
    public Page findByPage(int page, int pageSize, Long number, String beginTime, String endTime) {
        //构造分页构造器
        Page<Orders> ordersPage = new Page<>(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(number != null,Orders::getNumber,number)
                .gt(StringUtils.isNotEmpty(beginTime),Orders::getOrderTime,beginTime)
                .lt(StringUtils.isNotEmpty(endTime),Orders::getOrderTime,endTime);
        //执行查询
        return this.page(ordersPage,queryWrapper);
    }

    @Override
    //后台修改订单状态
    public void changeOrderStatus(Orders orders) {
        //获取订单状态
        Integer status = orders.getStatus();
        //修改订单状态
        if (status != 2){
            orders.setStatus(3);
        }
        //执行修改
        this.updateById(orders);
    }

    @Override
    //前台展示订单数据
    public Page showOrderByPage(int page, int pageSize) {
        List<OrdersDto> ordersDtoList;
        //构造分页构造器
        Page<Orders> ordersPage = new Page<>(page,pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件
        ordersLambdaQueryWrapper.orderByDesc(Orders::getOrderTime);
        //执行查询
        this.page(ordersPage, ordersLambdaQueryWrapper);
        //使用stream流为OrdersDto添加订单数据
        List<Orders> ordersList = ordersPage.getRecords();
        ordersDtoList = ordersList.stream().map((res) -> {
            OrdersDto ordersDto = new OrdersDto();
            //使用对象拷贝为其拷贝订单信息
            BeanUtils.copyProperties(res,ordersDto);
            //获取订单ID
            Long orderId = res.getId();
            //构造条件构造器
            LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId,orderId);
            List<OrderDetail> orderDetailList = orderDetailService.list(orderDetailLambdaQueryWrapper);
            //为OrdersDto添加数据
            ordersDto.setOrderDetails(orderDetailList);
            return ordersDto;
        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(ordersDtoList);
        return ordersDtoPage;
    }


}
