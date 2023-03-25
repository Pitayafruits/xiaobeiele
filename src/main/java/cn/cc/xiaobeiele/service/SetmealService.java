package cn.cc.xiaobeiele.service;

import cn.cc.xiaobeiele.dto.SetmealDto;
import cn.cc.xiaobeiele.pojo.Setmeal;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 套餐接口
 */

public interface SetmealService extends IService<Setmeal> {

    //新增套餐和关联的菜品信息
    void saveSetmealAndDish(SetmealDto setmealDto);

    //删除套餐和关联的菜品信息
    void deleteSetmealAndDish(List<Long> ids);

    //修改套餐销售状态
    void changeStatus(Integer status,List<Long> ids);

    //修改套餐时的数据回显
    SetmealDto findByIdAndDish(Long id);

    //修改套餐数据
    void updateSetmealAndDish(SetmealDto setmealDto);

    //分页展示套餐数据
    Page findByPage(int page,int pageSize,String name);

    //用户端显示套餐数据
    List<Setmeal> findByCaIdAndStatus(Setmeal setmeal);
}
