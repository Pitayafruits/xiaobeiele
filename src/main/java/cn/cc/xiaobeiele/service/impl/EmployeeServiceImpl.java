package cn.cc.xiaobeiele.service.impl;

import cn.cc.xiaobeiele.common.R;
import cn.cc.xiaobeiele.mapper.EmployeeMapper;
import cn.cc.xiaobeiele.pojo.Employee;
import cn.cc.xiaobeiele.service.EmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 员工Service
 */

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService {

    //登录方法
    @Override
    public R<Employee> login(HttpServletRequest request, Employee employee) {
        //1.加密前台传回的密码
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee employeeOne = this.getOne(queryWrapper);
        //3.如果没有查询到则返回登陆失败结果
        if (employeeOne == null){
            return R.error("该用户不存在，请检查用户名！");
        }
        //4.如果查到则去比对密码,如果密码错误则返回登陆失败结果
        if (!employeeOne.getPassword().equals(password)){
            return R.error("密码错误，请检查密码！");
        }
        //5.查询员工状态，如果员工已禁用，则返回禁用结果
        if (employeeOne.getStatus() == 0){
            return R.error("该账号已停用！");
        }
        //6.登陆成功，将员工id存入session并返回登陆成功结果
        request.getSession().setAttribute("employee",employeeOne.getId());
        return R.success(employeeOne);
    }

    //分页查询展示员工信息
    @Override
    public Page showEmployee(int page, int pageSize, String name) {
        //构造分页构造器
        Page<Employee> pageinfo = new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getCreateTime);
        //执行查询
        Page<Employee> employeePage = this.page(pageinfo, queryWrapper);
        return employeePage;
    }

}
