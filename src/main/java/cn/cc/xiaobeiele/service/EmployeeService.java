package cn.cc.xiaobeiele.service;

import cn.cc.xiaobeiele.common.R;
import cn.cc.xiaobeiele.pojo.Employee;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * 员工接口
 */

public interface EmployeeService extends IService<Employee> {

    //登录账户
    R<Employee> login(HttpServletRequest request, Employee employee);

    //分页显示用户
    Page showEmployee(int page,int pageSize,String name);
}
