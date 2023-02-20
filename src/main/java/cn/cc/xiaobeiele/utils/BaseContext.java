package cn.cc.xiaobeiele.utils;

/**
 * 基于ThreadLocal封装的工具类，用于保存和获取当前登录用户的id
 */

public class BaseContext {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    //保存id
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    //获取id
    public static Long getCurrentId(){
        return threadLocal.get();
    }

}
