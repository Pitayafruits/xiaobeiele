package cn.cc.xiaobeiele.common;

/**
 * 自定义业务异常处理
 */

public class CustomException extends RuntimeException{

    public CustomException(String msg){
        super(msg);
    }
}
