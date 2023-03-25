package cn.cc.xiaobeiele.service.impl;

import cn.cc.xiaobeiele.mapper.ShoppingCartMapper;
import cn.cc.xiaobeiele.pojo.ShoppingCart;
import cn.cc.xiaobeiele.service.ShoppingCartService;
import cn.cc.xiaobeiele.utils.BaseContext;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    //添加菜品或套餐进购物车
    @Override
    public ShoppingCart addCart(ShoppingCart shoppingCart) {
        //为当前购物车设置用户ID
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        //查询菜品或套餐ID判断菜品或套餐是否在购物车中
        //根据菜品ID是否获取到值来判断当前添加的是菜品还是套餐
        Long dishId = shoppingCart.getDishId();
        //构造条件构造器
        LambdaQueryWrapper<ShoppingCart> shoppingCartQueryWrapper = new LambdaQueryWrapper<>();
        //为查询添加用户ID
        shoppingCartQueryWrapper.eq(ShoppingCart::getUserId,userId);
        if (dishId != null){
            //添加进购物车的是菜品
            //设置菜品ID为条件
            shoppingCartQueryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            //添加进购物车的是套餐
            //设置套餐ID为条件
            shoppingCartQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //执行查询
        ShoppingCart cartOne = this.getOne(shoppingCartQueryWrapper);
        //根据查询结果执行购物车+1还是添加进购物车
        if (cartOne != null){
            //购物车里已经有同样东西,使用修改语句给数量+1
            Integer dateNumber = cartOne.getNumber();
            cartOne.setNumber(dateNumber + 1);
            this.updateById(cartOne);
        }else {
            //如果购物车没有则执行插入语句添加进购物车，数量默认1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            this.save(shoppingCart);
            cartOne = shoppingCart;
        }
        return cartOne;
    }

    //查看购物车
    @Override
    public List<ShoppingCart> listByCart() {
        //构造条件构造器
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加条件 用户ID和排序条件
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartLambdaQueryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        //执行查询
        return this.list(shoppingCartLambdaQueryWrapper);
    }

    //购物车数据减少
    @Override
    public ShoppingCart subCart(ShoppingCart shoppingCart) {
        //为当前购物车设置用户ID
        Long userID = BaseContext.getCurrentId();
        shoppingCart.setUserId(userID);
        //根据购物车内获取到值来判断当前添加的是菜品还是套餐
        Long dishId = shoppingCart.getDishId();
        //构造条件构造器
        LambdaQueryWrapper<ShoppingCart> shoppingCartQueryWrapper = new LambdaQueryWrapper<>();
        //为查询添加用户ID
        shoppingCartQueryWrapper.eq(ShoppingCart::getUserId,userID);
        if (dishId != null){
            //说明当前操作的是菜品减一
            //设置菜品ID
            shoppingCartQueryWrapper.eq(ShoppingCart::getDishId,dishId);
            //对应菜品减一
        }else {
            //说明当前操作的是套餐减一
            //设置套餐ID
            shoppingCartQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //执行查询
        ShoppingCart cartOne = this.getOne(shoppingCartQueryWrapper);
        //购物车肯定不为空
        if (cartOne != null){
            //购物车里已经有同样东西,使用修改语句给数量-1
            Integer dateNumber = cartOne.getNumber();
            //当一个数量减到为0时，从购物车中删除
            if (dateNumber <= 0){
                //首先判断执行删除的是菜品还是套餐
                Long dishTarget = cartOne.getDishId();
                //构造条件构造器
                LambdaQueryWrapper<ShoppingCart> removeCartWrapper = new LambdaQueryWrapper<>();
                //添加用户ID
                removeCartWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
                //进行判断
                if (dishTarget != null){
                    //添加菜品ID
                    removeCartWrapper.eq(ShoppingCart::getDishId,cartOne.getDishId());
                }else{
                    //添加套餐ID
                    removeCartWrapper.eq(ShoppingCart::getSetmealId,cartOne.getSetmealId());
                }
                //执行删除
                this.remove(removeCartWrapper);
            }
            cartOne.setNumber(dateNumber - 1);
            this.updateById(cartOne);
        }
        cartOne = shoppingCart;
        return cartOne;
    }


    //清空购物车
    @Override
    public boolean cleanCart() {
        //构造条件构造器
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加用户ID
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        //执行删除操作
        return this.remove(shoppingCartLambdaQueryWrapper);
    }


}
