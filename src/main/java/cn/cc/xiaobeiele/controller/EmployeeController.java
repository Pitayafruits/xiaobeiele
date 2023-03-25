package cn.cc.xiaobeiele.controller;

import cn.cc.xiaobeiele.common.R;
import cn.cc.xiaobeiele.pojo.Employee;
import cn.cc.xiaobeiele.service.EmployeeService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 员工Controller
 */

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    //员工登录
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        R<Employee> employeeR = employeeService.login(request, employee);
        return employeeR;
    }

    //员工登出
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        log.info("员工登出");
        request.getSession().removeAttribute("employee");
        return R.success("退出成功！");
    }

    //添加员工
    @PostMapping
    public R<String> addEmployee(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工信息为{}",employee.toString());
        //为新增员工设置初始密码并加密
        employee.setPassword(DigestUtils.md5DigestAsHex("cc123456".getBytes()));
        //调用service保存用户
        employeeService.save(employee);
        return R.success("添加成功!");
    }

    //分页查询展示员工信息
    @GetMapping("/page")
    public R<Page> showEmployee(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        Page showEmployee = employeeService.showEmployee(page, pageSize, name);
        return R.success(showEmployee);
    }

    //启用或禁用员工账号
    @PutMapping
    public R<String> updateStatus(@RequestBody Employee employee){
        log.info(employee.toString());
        employeeService.updateById(employee);
        return R.success("修改成功！");
    }

    //根据id查询员工信息用来做编辑员工时的数据回显
    @GetMapping("{id}")
    public R<Employee> findById(@PathVariable Long id){
        log.info("根据员工id查询信息...");
        Employee employee = employeeService.getById(id);
        if (employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到员工");
    }

}
