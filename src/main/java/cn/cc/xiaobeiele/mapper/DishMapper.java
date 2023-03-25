package cn.cc.xiaobeiele.mapper;

import cn.cc.xiaobeiele.pojo.Dish;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜品DAO
 */

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
