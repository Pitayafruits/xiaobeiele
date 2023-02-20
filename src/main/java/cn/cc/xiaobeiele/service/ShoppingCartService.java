package cn.cc.xiaobeiele.service;

import cn.cc.xiaobeiele.pojo.ShoppingCart;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ShoppingCartService extends IService<ShoppingCart> {

    //添加菜品或套餐进购物车
    ShoppingCart addCart(ShoppingCart shoppingCart);

    //查看购物车
    List<ShoppingCart> listByCart();

    //购物车数据减少
    ShoppingCart subCart(ShoppingCart shoppingCart);

    //清空购物车
    boolean cleanCart();
}
