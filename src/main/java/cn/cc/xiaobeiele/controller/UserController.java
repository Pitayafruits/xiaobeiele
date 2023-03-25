package cn.cc.xiaobeiele.controller;

import cn.cc.xiaobeiele.common.R;
import cn.cc.xiaobeiele.pojo.User;
import cn.cc.xiaobeiele.service.UserService;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    //发送登录验证码
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user){
        log.info("user:{}",user);
        String userPhone = user.getPhone();
        if (StringUtils.isNotEmpty(userPhone)){
            userService.sendMsg(user);
            return R.success("发送短信成功！");
        }
        return R.error("请输入正确的手机号！");
    }

    //手机用户登录
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession session){
        log.info(map.toString());
        User user = userService.login(map,session);
        if (user != null){
            return R.success(user);
        }
        return R.error("登陆失败！");
    }


}
