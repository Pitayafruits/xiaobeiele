package cn.cc.xiaobeiele.service;

import cn.cc.xiaobeiele.dto.DishDto;
import cn.cc.xiaobeiele.pojo.Dish;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 菜品接口
 */

public interface DishService extends IService<Dish> {

    //新增菜品和对应的口味，插入两张表
    void saveDishAndFlavors(DishDto dishDto);

    //根据ID查询对应的菜品和口味信息
    DishDto findByIdAndFlavors(Long id);

    //修改菜品和口味信息
    void updateDishAndFlavors(DishDto dishDto);

    //修改菜品的销售状态
    void changeStatus(Integer status,List<Long> ids);

    //根据id删除菜品和口味
    void removeDishAndFavor(List<Long> ids);

    //分页展示套餐数据
    Page findByPage(int page,int pageSize,String name);

    //新增套餐时添加菜品的查询菜品（方法升级->适应客户端的显示菜品的口味信息）
    List<DishDto> findByCaIdAndFla(Dish dish);
}
