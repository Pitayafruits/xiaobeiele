package cn.cc.xiaobeiele.controller;

import cn.cc.xiaobeiele.common.R;
import cn.cc.xiaobeiele.pojo.Category;
import cn.cc.xiaobeiele.service.CategoryService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类Controller
 */

@Slf4j
@RestController
@RequestMapping("/category")
public class CategroyController {

    @Autowired
    private CategoryService categoryService;

    //新增分类
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("Category:{}",category);
        categoryService.save(category);
        return R.success("新增成功！");
    }

    //分页查询分类
    @GetMapping("/page")
    public R<Page> showCategory(int page,int pageSize){
        log.info("page={},pageSize={}",page,pageSize);
        Page categoryByPage = categoryService.categoryByPage(page, pageSize);
        return R.success(categoryByPage);
    }

    //删除分类
    @DeleteMapping
    public R<String> deleteCategory(Long id){
        log.info("删除的分类id为：{}",id);
        //在删除之前应该先判断要删除的分类是否关联套餐与菜品
        boolean flag = categoryService.remove(id);
        if (flag){
            return R.success("删除成功！");
        }
        return R.success("删除失败！！");
    }

    //修改分类
    @PutMapping
    public R<String> updateCategory(@RequestBody Category category){
        log.info("Category:{}",category);
        categoryService.updateById(category);
        return R.success("修改成功！");
    }

    //添加菜品查询菜品分类
    @GetMapping("/list")
    public R<List<Category>> selectList(Category category){
        log.info("Category:{}",category);
        List<Category> categoryList = categoryService.selectList(category);
        return R.success(categoryList);
    }
}
