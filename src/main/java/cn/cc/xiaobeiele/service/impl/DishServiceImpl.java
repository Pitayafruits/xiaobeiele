package cn.cc.xiaobeiele.service.impl;

import cn.cc.xiaobeiele.common.CustomException;
import cn.cc.xiaobeiele.common.R;
import cn.cc.xiaobeiele.dto.DishDto;
import cn.cc.xiaobeiele.mapper.DishMapper;
import cn.cc.xiaobeiele.pojo.Category;
import cn.cc.xiaobeiele.pojo.Dish;
import cn.cc.xiaobeiele.pojo.DishFlavor;
import cn.cc.xiaobeiele.service.CategoryService;
import cn.cc.xiaobeiele.service.DishFlavorService;
import cn.cc.xiaobeiele.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 菜品接口实现类
 */

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    //新增菜品和对应的口味，插入两张表
    @Override
    @Transactional
    public void saveDishAndFlavors(DishDto dishDto) {
        //先操作dish表插入菜品基本信息
        this.save(dishDto);
        //然后操作dishflavors表存入口味信息
        //需要先对数据进行改造，把dish_id也要存进去
        Long dishDtoId = dishDto.getId(); //菜品ID
        //在菜品口味集合中遍历加上菜品ID的值
        List<DishFlavor> dishFlavors = dishDto.getFlavors();
        dishFlavors.stream().map((res) -> {
            res.setDishId(dishDtoId);
            return res;
        }).collect(Collectors.toList());
        //操作dish_flavors表存入口味
        dishFlavorService.saveBatch(dishDto.getFlavors());
        //清理某个分类下菜品的缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
    }

    //根据ID查询对应的菜品和口味信息
    @Override
    @Transactional
    public DishDto findByIdAndFlavors(Long id) {
        //首先查询菜品信息
        Dish dish = this.getById(id);
        //通过拷贝将菜品基本信息传到DishDto
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //然后查询当前菜品对应的口味信息
        //构造条件构造器
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        //通过菜品ID查询口味信息
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper);
        //口味数据封装到dishDto里
        dishDto.setFlavors(dishFlavorList);
        return dishDto;
    }

    //修改菜品和口味信息
    @Override
    @Transactional
    public void updateDishAndFlavors(DishDto dishDto) {
        //修改菜品基本信息
        this.updateById(dishDto);
        //清理对应的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //保存当前添加的口味信息
        List<DishFlavor> dishFlavorList = dishDto.getFlavors();
        //为口味信息添加菜品ID
        dishFlavorList = dishFlavorList.stream().map((res) -> {
            res.setDishId(dishDto.getId());
            return res;
        }).collect(Collectors.toList());
        //调用service保存
        dishFlavorService.saveBatch(dishFlavorList);
        //清理所有菜品的缓存数据
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
    }

    //修改菜品的销售状态
    @Override
    public void changeStatus(Integer status, List<Long> ids) {
        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //条件匹配多字段
        queryWrapper.in(Dish::getId,ids);
        List<Dish> dishList = this.list(queryWrapper);
        //使用stream流修改status字段
        dishList = dishList.stream().map((res) -> {
            res.setStatus(status);
            return res;
        }).collect(Collectors.toList());
        this.updateBatchById(dishList);
        //清理所有菜品的缓存数据
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
    }

    //删除菜品和口味信息
    @Override
    @Transactional
    public void removeDishAndFavor(List<Long> ids) {
        //判断当前菜品是否在售
        //构造菜品条件构造器
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        //菜品匹配状态字段和id字段
        dishQueryWrapper.in(Dish::getId,ids);
        dishQueryWrapper.eq(Dish::getStatus,1);
        //查询菜品组的状态
        long count = this.count(dishQueryWrapper);
        //如果在售，则抛出一个业务异常
        if (count > 0){
            throw new CustomException("删除失败，菜品在售中");
        }
        //如果不在售，可以删除
        this.removeByIds(ids);
        //构造菜品口味构造器
        LambdaQueryWrapper<DishFlavor> dishFlavorQueryWrapper = new LambdaQueryWrapper<>();
        //通过菜品ID找到关联口味删除
        dishFlavorQueryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(dishFlavorQueryWrapper);
    }

    //分页展示菜品数据
    @Override
    @Transactional
    public Page findByPage(int page, int pageSize, String name) {
        //构造分页构造器
        Page<Dish> dishPage = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行查询
        this.page(dishPage,queryWrapper);
        //对象拷贝，第一次拷贝不需要列表数据，只需要分页信息
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");
        //对列表数据进行处理
        List<Dish> dishRecords = dishPage.getRecords();
        //创建一个dishDto的集合来接收处理完的数据
        List<DishDto> dishDtoList = dishRecords.stream().map((res) -> {
            DishDto dishDto = new DishDto();
            //继续使用对象拷贝，第二次拷贝菜品信息，为其设置分类名称
            BeanUtils.copyProperties(res,dishDto);
            //通过分类ID查询分类名称
            Long categoryId = res.getCategoryId();
            Category category = categoryService.getById(categoryId);
            //判断分类是否存在
            if (category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        //最后把集合的值给分页对象
        dishDtoPage.setRecords(dishDtoList);
        return dishDtoPage;
    }

    //新增套餐时添加菜品的查询菜品（方法升级->适应客户端的显示菜品的口味信息）
    @Override
    public List<DishDto> findByCaIdAndFla(Dish dish) {
        List<DishDto> dishDtoList = null;
        //动态构造key
        String key = "dish" + "_" + dish.getCategoryId() + "_" + dish.getStatus();
        //从redis中获取菜品数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        //如果获取到，直接返回
        if (dishDtoList != null){
            return dishDtoList;
        }
        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        //在添加个状态条件
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //执行查询
        List<Dish> dishList = this.list(queryWrapper);
        //使用stream流为dishDto添加口味数据
        dishDtoList = dishList.stream().map((res) -> {
            DishDto dishDto = new DishDto();
            //使用对象拷贝为其拷贝菜品信息
            BeanUtils.copyProperties(res,dishDto);
            //通过分类ID查询分类名称
            Long categoryId = res.getCategoryId();
            Category category = categoryService.getById(categoryId);
            //判断分类是否存在
            if (category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //通过菜品ID查询口味信息
            Long dishId = res.getId();
            //构造条件构造器
            LambdaQueryWrapper<DishFlavor> dishFlavorQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavors = dishFlavorService.list(dishFlavorQueryWrapper);
            //为dishDto添加口味数据
            dishDto.setFlavors(dishFlavors);
            return dishDto;
        }).collect(Collectors.toList());
        //如果不存在，则查询数据库，并将查询到的数据存入Redis中
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);
        return dishDtoList;
    }


}
