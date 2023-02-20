package cn.cc.xiaobeiele.service.impl;

import cn.cc.xiaobeiele.mapper.AddressBookMapper;
import cn.cc.xiaobeiele.pojo.AddressBook;
import cn.cc.xiaobeiele.service.AddressBookService;
import cn.cc.xiaobeiele.utils.BaseContext;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

    //设置指定默认地址(明确一个用户可以有多个地址，但默认地址唯一)
    @Override
    public AddressBook setDefaultAddress(AddressBook addressBook) {
        log.info("addressBook:{}", addressBook);
        //构造条件构造器
        LambdaUpdateWrapper<AddressBook> addressBookQueryWrapper = new LambdaUpdateWrapper<>();
        //通过用户id将所有地址的状态置为0
        addressBookQueryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        addressBookQueryWrapper.set(AddressBook::getIsDefault,0);
        this.update(addressBookQueryWrapper);
        //再将用户选择的地址选为默认地址
        addressBook.setIsDefault(1);
        //最后通过id执行一次修改
        this.updateById(addressBook);
        return addressBook;
    }

    //查询指定用户默认地址
    @Override
    public AddressBook getDefaultAddress() {
        //构造条件构造器
        LambdaQueryWrapper<AddressBook> addressBookQueryWrapper = new LambdaQueryWrapper<>();
        //根据用户id并且状态位为1的地址
        addressBookQueryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        addressBookQueryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = this.getOne(addressBookQueryWrapper);
        return addressBook;
    }

    //查询指定用户的全部地址
    @Override
    public List<AddressBook> getAllAddress(AddressBook addressBook) {
        //构造条件构造器
        LambdaQueryWrapper<AddressBook> addressBookQueryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        addressBookQueryWrapper.eq(addressBook.getUserId() != null,AddressBook::getUserId,addressBook.getUserId());
        //添加排序条件
        addressBookQueryWrapper.orderByDesc(AddressBook::getUpdateTime);
        //执行操作
        List<AddressBook> addressBookList = this.list(addressBookQueryWrapper);
        return addressBookList;
    }

    //删除地址信息
    @Override
    public boolean deleteAddress(List<Long> ids) {
        //构造条件构造器
        LambdaQueryWrapper<AddressBook> addressBookQueryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        addressBookQueryWrapper.in(AddressBook::getId,ids);
        //执行删除
        return this.remove(addressBookQueryWrapper);
    }

    //修改地址信息
    @Override
    public boolean updateAddress(AddressBook addressBook) {
        //修改地址
        return this.updateById(addressBook);
    }


}
