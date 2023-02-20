package cn.cc.xiaobeiele.service.impl;

import cn.cc.xiaobeiele.common.CustomException;
import cn.cc.xiaobeiele.mapper.CategoryMapper;
import cn.cc.xiaobeiele.pojo.Category;
import cn.cc.xiaobeiele.pojo.Dish;
import cn.cc.xiaobeiele.pojo.Setmeal;
import cn.cc.xiaobeiele.service.CategoryService;
import cn.cc.xiaobeiele.service.DishService;
import cn.cc.xiaobeiele.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 分类Service
 */

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements CategoryService {


    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;


    //删除方法
    @Override
    @Transactional
    public boolean remove(Long id) {
        //构造一个菜品条件构造器，通过构造器构造的条件查询其是否有分类id
        LambdaQueryWrapper<Dish> dishqueryWrapper = new LambdaQueryWrapper<>();
        dishqueryWrapper.eq(Dish::getCategoryId,id);
        long dishCount = dishService.count(dishqueryWrapper);
        //构造一个套餐条件构造器，通过构造器构造的条件查询其是否有分类id
        LambdaQueryWrapper<Setmeal> setmealqueryWrapper = new LambdaQueryWrapper<>();
        setmealqueryWrapper.eq(Setmeal::getCategoryId,id);
        long setmealCount = setmealService.count(setmealqueryWrapper);
        //查询当前分类是否关联菜品，如果关联，抛出一个业务异常
        if (dishCount > 0){
            throw new CustomException("当前分类关联菜品，不能删除！");
        }
        //查询当前分类是否关联套餐。如果关联。抛出一个业务异常
        if (setmealCount > 0){
            throw new CustomException("当前分类关联套餐，不能删除！");
        }
        //都不关联，执行删除操作
        return super.removeById(id);
    }

    //分类的分页查询
    @Override
    public Page categoryByPage(int page, int pageSize) {
        //构造分页构造器
        Page<Category> pageInfo = new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort);
        //执行查询
        Page<Category> categoryPage = this.page(pageInfo, queryWrapper);
        return categoryPage;
    }

    //添加菜品查询菜品分类
    @Override
    public List<Category> selectList(Category category) {
        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        //添加条件
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getType).orderByDesc(Category::getUpdateTime);
        //执行查询
        List<Category> categoryList = this.list(queryWrapper);
        return categoryList;
    }

}
