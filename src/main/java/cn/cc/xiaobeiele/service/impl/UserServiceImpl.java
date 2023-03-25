package cn.cc.xiaobeiele.service.impl;

import cn.cc.xiaobeiele.mapper.UserMapper;
import cn.cc.xiaobeiele.pojo.User;
import cn.cc.xiaobeiele.service.UserService;
import cn.cc.xiaobeiele.utils.SMSUtils;
import cn.cc.xiaobeiele.utils.ValidateCodeUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private RedisTemplate redisTemplate;

    //发送手机用户登录短信
    @Override
    public void sendMsg(User user) {
        //获得登录手机号
        String phone = user.getPhone();
        //生成验证码
        Integer code = ValidateCodeUtils.generateValidateCode(4);
        //调用腾讯云提供的短信服务API
        SMSUtils.sendMessage(phone, code.toString(), SMSUtils.templateId_validate);
        //将验证码存到Redis中
        redisTemplate.opsForValue().set(phone,code.toString(),5, TimeUnit.MINUTES);
    }

    //手机用户登录
    @Override
    public User login(Map map, HttpSession session) {
        //获得用户的手机号
        String phone = map.get("phone").toString();
        //获得验证码
        String code = map.get("code").toString();
        log.info("验证码为:{}",code);
        //取出Redis中存入的验证码
        Object codeInRedis = redisTemplate.opsForValue().get(phone);
        //比对用户输入的验证码与存入redis中的验证码
        if (codeInRedis != null && code.equals(codeInRedis)) {
            //如果正确登录成功
            //构造条件构造器根据手机号查询用户表
            LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
            //添加条件
            userQueryWrapper.eq(User::getPhone, phone);
            //执行查询
            User user = this.getOne(userQueryWrapper);
            //如果没结果，则去完成新用户注册
            if (user == null) {
                //比对手机号是否在用户表中，如果不在自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                user.setName("手机用户" + phone);
                this.save(user);
            }
            //登录信息存到session中
            session.setAttribute("user",user.getId());
            //如果登录成功，则删除Redis中缓存的验证码
            redisTemplate.delete(phone);
            return user;
        }
        return null;
    }

}
