package cn.cc.xiaobeiele.service;

import cn.cc.xiaobeiele.pojo.Category;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 分类接口
 */

public interface CategoryService extends IService<Category> {

    //删除方法，判断分类与套餐和菜品之间是否有级联
    boolean remove(Long ids);

    //分类的分页查询
    Page categoryByPage(int page,int pageSize);

    //添加菜品查询菜品分类
    List<Category>  selectList(Category category);
}
