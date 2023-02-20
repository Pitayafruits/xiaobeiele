package cn.cc.xiaobeiele.service.impl;

import cn.cc.xiaobeiele.mapper.OrderDetailMapper;
import cn.cc.xiaobeiele.pojo.OrderDetail;
import cn.cc.xiaobeiele.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}
