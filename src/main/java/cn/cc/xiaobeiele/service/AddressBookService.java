package cn.cc.xiaobeiele.service;

import cn.cc.xiaobeiele.pojo.AddressBook;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


public interface AddressBookService extends IService<AddressBook> {

    //设置指定用户默认地址
    AddressBook setDefaultAddress(AddressBook addressBook);

    //查询指定用户默认地址
    AddressBook getDefaultAddress();

    //查询指定用户的全部地址
    List<AddressBook> getAllAddress(AddressBook addressBook);

    //删除地址信息
    boolean deleteAddress(List<Long> ids);

    //修改地址信息
    boolean updateAddress(AddressBook addressBook);

}
