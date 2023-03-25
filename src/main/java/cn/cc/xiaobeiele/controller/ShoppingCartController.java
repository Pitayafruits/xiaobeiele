package cn.cc.xiaobeiele.controller;

import cn.cc.xiaobeiele.common.R;
import cn.cc.xiaobeiele.pojo.ShoppingCart;
import cn.cc.xiaobeiele.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    //添加菜品或套餐进购物车
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车数据:{}",shoppingCart);
        ShoppingCart cartOrder = shoppingCartService.addCart(shoppingCart);
        return R.success(cartOrder);
    }

    //查看购物车
    @GetMapping("/list")
    public R<List<ShoppingCart>> showCart(){
        log.info("查看购物车数据：");
        List<ShoppingCart> shoppingCartList = shoppingCartService.listByCart();
        return R.success(shoppingCartList);
    }

    //购物车数据减少
    @PostMapping("/sub")
    public R<ShoppingCart> subCart(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车数据减1...");
        ShoppingCart subCart = shoppingCartService.subCart(shoppingCart);
        return R.success(subCart);
    }

    //清空购物车
    @DeleteMapping("/clean")
    public R<String> cleanCart(){
        log.info("清空购物车....");
        boolean flag = shoppingCartService.cleanCart();
        if (flag){
            return R.success("清空成功！");
        }
        return R.error("清空失败！");
    }
}
