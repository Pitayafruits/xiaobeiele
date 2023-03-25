package cn.cc.xiaobeiele.controller;

import cn.cc.xiaobeiele.common.R;
import cn.cc.xiaobeiele.dto.DishDto;
import cn.cc.xiaobeiele.pojo.Dish;
import cn.cc.xiaobeiele.service.DishService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 菜品管理
 */

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;


    //新增菜品
    @PostMapping
    public R<String> addDish(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveDishAndFlavors(dishDto);
        return R.success("新增成功！");
    }

    //分页展示菜品数据
    @GetMapping("/page")
    public R<Page> pageDish(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        Page dishServiceByPage = dishService.findByPage(page, pageSize, name);
        return R.success(dishServiceByPage);
    }

    //根据id查询菜品及其口味信息
    @GetMapping("/{id}")
    public R<DishDto> showDish(@PathVariable Long id){
        log.info("id:{}",id);
        //修改菜品时的数据回显
        DishDto dishDto = dishService.findByIdAndFlavors(id);
        return R.success(dishDto);
    }

    //修改菜品
    @PutMapping
    public R<String> updateDish(@RequestBody DishDto dishDto){
        log.info("dishDto:{}",dishDto);
        dishService.updateDishAndFlavors(dishDto);
        return R.success("修改成功！");
    }

    //修改菜品销售状态
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable("status") Integer status,@RequestParam("ids") List<Long> ids){
        log.info("status:{},ids:{}",status,ids);
        dishService.changeStatus(status,ids);
        return R.success("修改成功！");
    }

    //删除菜品
    @DeleteMapping
    public R<String> deleteDish(@RequestParam List<Long> ids){
        log.info("ids:{}",ids);
        dishService.removeDishAndFavor(ids);
        return R.success("删除成功！");
    }


    //新增套餐时添加菜品的查询菜品（方法升级->适应客户端的显示菜品的口味信息）
    @GetMapping("/list")
    public R<List<DishDto>> findByCategroyId(Dish dish){
        log.info("dish:{}",dish);
        List<DishDto> dishDtoList = dishService.findByCaIdAndFla(dish);
        return R.success(dishDtoList);
    }

}
