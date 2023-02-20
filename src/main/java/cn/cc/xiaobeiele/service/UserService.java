package cn.cc.xiaobeiele.service;

import cn.cc.xiaobeiele.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;


import javax.servlet.http.HttpSession;
import java.util.Map;

public interface UserService extends IService<User> {

    //发送手机用户登录短信
    void sendMsg(User user);

    //手机用户登录
    User login(Map map,HttpSession session);
}
