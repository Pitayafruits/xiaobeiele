package cn.cc.xiaobeiele.service.impl;

import cn.cc.xiaobeiele.common.CustomException;
import cn.cc.xiaobeiele.dto.SetmealDto;
import cn.cc.xiaobeiele.mapper.SetmealMapper;
import cn.cc.xiaobeiele.pojo.Category;
import cn.cc.xiaobeiele.pojo.Setmeal;
import cn.cc.xiaobeiele.pojo.SetmealDish;
import cn.cc.xiaobeiele.service.CategoryService;
import cn.cc.xiaobeiele.service.SetmealDishService;
import cn.cc.xiaobeiele.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐接口实现类
 */

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    //新增套餐和菜品信息
    @Override
    @Transactional
    @CacheEvict(value = "SetmealCache",allEntries = true)
    public void saveSetmealAndDish(SetmealDto setmealDto) {
        //保存套餐信息
        this.save(setmealDto);
        //保存套餐关联的菜品信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //使用stream流为其添加套餐ID
        setmealDishes.stream().map((res) -> {
            res.setSetmealId(setmealDto.getId());
            return res;
        }).collect(Collectors.toList());
        //执行
        setmealDishService.saveBatch(setmealDishes);
    }

    //删除套餐和关联的菜品信息
    @Override
    @Transactional
    @CacheEvict(value = "SetmealCache",allEntries = true)
    public void deleteSetmealAndDish(List<Long> ids) {
        //构造套餐条件构造器
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        //根据id聚合查询状态为1的套餐
        setmealQueryWrapper.in(Setmeal::getId,ids);
        setmealQueryWrapper.eq(Setmeal::getStatus,1);
        //根据查询结果判断套餐是否在售
        long count = this.count(setmealQueryWrapper);
        //如果套餐为在售，则抛出一个业务异常
        if (count > 0){
            throw new CustomException("套餐在售中，不能删除！");
        }
        //如果套餐不在售，首先根据套餐id删除套餐基本信息
        this.removeByIds(ids);
        //然后删除其关联的菜品信息，构造关联菜品条件构造器
        LambdaQueryWrapper<SetmealDish> setmealDishQueryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        setmealDishQueryWrapper.in(SetmealDish::getSetmealId,ids);
        //执行删除
        setmealDishService.remove(setmealDishQueryWrapper);
    }

    //修改套餐销售状态
    @Override
    @CacheEvict(value = "SetmealCache",allEntries = true)
    public void changeStatus(Integer status, List<Long> ids) {
        //构造条件构造器
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        //条件匹配多字段
        setmealQueryWrapper.in(Setmeal::getId,ids);
        //批量查询符合条件
        List<Setmeal> setmealList = this.list(setmealQueryWrapper);
        //使用stream流更新status字段
        setmealList = setmealList.stream().map((res) -> {
            res.setStatus(status);
            return res;
        }).collect(Collectors.toList());
        setmealService.updateBatchById(setmealList);
    }


    //根据套餐id查询套餐的基本信息及其关联的菜品信息
    @Override
    @Transactional
    public SetmealDto findByIdAndDish(Long id) {
        //先查套餐的基本信息
        Setmeal setmeal = setmealService.getById(id);
        //将基本信息拷贝到setmealDto中
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        //然后查询套餐关联的菜品信息,构造条件构造器
        LambdaQueryWrapper<SetmealDish> setmealDishQueryWrapper = new LambdaQueryWrapper<>();
        //通过套餐ID查询菜品信息
        setmealDishQueryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> setmealDishList = setmealDishService.list(setmealDishQueryWrapper);
        //口味数据封装到setmealDto中
        setmealDto.setSetmealDishes(setmealDishList);
        return setmealDto;
    }

    //修改套餐和关联的菜品信息
    @Override
    @Transactional
    @CacheEvict(value = "SetmealCache",allEntries = true)
    public void updateSetmealAndDish(SetmealDto setmealDto) {
        //修改套餐的基本信息
        this.updateById(setmealDto);
        //清除关联的菜品信息
        LambdaQueryWrapper<SetmealDish> setmealDishQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishQueryWrapper.like(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(setmealDishQueryWrapper);
        //保存当前添加的菜品信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //为菜品信息添加套餐ID
        setmealDishes = setmealDishes.stream().map((res) -> {
            res.setSetmealId(setmealDto.getId());
            return res;
        }).collect(Collectors.toList());
        //调用service保存
        setmealDishService.saveBatch(setmealDishes);
    }

    //分页展示套餐数据
    @Override
    @Transactional
    public Page findByPage(int page, int pageSize, String name) {
        //构造分页构造器
        Page<Setmeal> setmealPage = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null,Setmeal::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getCreateTime);
        //执行查询
        setmealService.page(setmealPage,queryWrapper);
        //对象拷贝，第一次拷贝不需要列表数据，只需要分页信息
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
        //对列表数据进行处理
        List<Setmeal> setmealRecords = setmealPage.getRecords();
        //创建一个setmealDto的集合来接收处理完的数据
        List<SetmealDto> setmealDtoList = setmealRecords.stream().map((res) -> {
            SetmealDto setmealDto = new SetmealDto();
            //继续使用对象拷贝，第二次拷贝套餐信息，为其设置分类名称
            BeanUtils.copyProperties(res,setmealDto);
            //通过分类ID查询分类名称
            Long categoryId = res.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            setmealDto.setCategoryName(categoryName);
            return setmealDto;
        }).collect(Collectors.toList());
        //最后把集合的值给分页对象
        setmealDtoPage.setRecords(setmealDtoList);
        return setmealDtoPage;
    }

    //用户端显示套餐数据
    @Override
    @Cacheable(value = "SetmealCache",key = "#setmeal.categoryId + '_' + #setmeal.status")
    public List<Setmeal> findByCaIdAndStatus(Setmeal setmeal) {
        //构造条件构造器
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件(套餐ID 和 在售状态1)
        setmealQueryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        setmealQueryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,1);
        //执行操作
        List<Setmeal> setmealList = this.list(setmealQueryWrapper);
        return setmealList;
    }


}
