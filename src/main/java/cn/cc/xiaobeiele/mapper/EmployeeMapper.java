package cn.cc.xiaobeiele.mapper;

import cn.cc.xiaobeiele.pojo.Employee;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 员工DAO
 */

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}
