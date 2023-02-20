package cn.cc.xiaobeiele.controller;


import cn.cc.xiaobeiele.common.R;
import cn.cc.xiaobeiele.dto.SetmealDto;
import cn.cc.xiaobeiele.pojo.Setmeal;
import cn.cc.xiaobeiele.service.SetmealService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 套餐控制
 */

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    //新增套餐
    @PostMapping
    public R<String> saveSetmeal(@RequestBody SetmealDto setmealDto){
        log.info("setmealDto:{}",setmealDto);
        setmealService.saveSetmealAndDish(setmealDto);
        return R.success("新增成功！");
    }

    //分页展示套餐数据
    @GetMapping("/page")
    public R<Page> pageForSetmeal(int page,int pageSize,String name){
        log.info("page:{},pageSize:{},name:{},",page,pageSize,name);
        Page setmealServiceByPage = setmealService.findByPage(page, pageSize, name);
        return R.success(setmealServiceByPage);
    }

    //删除套餐
    @DeleteMapping
    public R<String> deleteSetmeal(@RequestParam List<Long> ids){
        log.info("ids:{}",ids);
        setmealService.deleteSetmealAndDish(ids);
        return R.success("删除成功！");
    }

    //修改套餐销售状态
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable("status") Integer status,@RequestParam("ids") List<Long> ids){
        log.info("status:{},ids:{}",status,ids);
        setmealService.changeStatus(status,ids);
        return R.success("修改成功！");
    }

    //根据套餐id查询套餐的基本信息及其关联的菜品信息
    @GetMapping("/{id}")
    public R<SetmealDto> showSetmeal(@PathVariable Long id){
        log.info("id:{}",id);
        //修改菜品时的数据回显
        SetmealDto setmealDto = setmealService.findByIdAndDish(id);
        return R.success(setmealDto);
    }

    //修改套餐
    @PutMapping
    public R<String> updateSetmeal(@RequestBody SetmealDto setmealDto){
        log.info("setmealDto:{}",setmealDto);
        setmealService.updateSetmealAndDish(setmealDto);
        return R.success("修改成功！");
    }

    //用户端显示套餐数据
    @GetMapping("/list")
    public R<List<Setmeal>> findByCaIdAndStatus(Setmeal setmeal){
        log.info("setmeal:{}",setmeal);
        List<Setmeal> setmealList = setmealService.findByCaIdAndStatus(setmeal);
        return R.success(setmealList);
    }

}
