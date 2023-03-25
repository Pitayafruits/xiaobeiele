package cn.cc.xiaobeiele.dto;

import cn.cc.xiaobeiele.pojo.Dish;
import cn.cc.xiaobeiele.pojo.DishFlavor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
public class DishDto extends Dish {

    //菜品关联的口味数据集合
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
