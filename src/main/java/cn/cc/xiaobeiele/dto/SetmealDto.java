package cn.cc.xiaobeiele.dto;


;
import cn.cc.xiaobeiele.pojo.Setmeal;
import cn.cc.xiaobeiele.pojo.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
