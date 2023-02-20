package cn.cc.xiaobeiele.controller;

import cn.cc.xiaobeiele.common.R;
import cn.cc.xiaobeiele.pojo.AddressBook;
import cn.cc.xiaobeiele.service.AddressBookService;
import cn.cc.xiaobeiele.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址簿管理
 */

@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    //新增地址
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        //设置用户ID
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    //设置默认地址
    @PutMapping("default")
    public R<AddressBook> setDefaultAddress(@RequestBody AddressBook addressBook) {
        log.info("addressBook:{}", addressBook);
        AddressBook newAddressBook = addressBookService.setDefaultAddress(addressBook);
        return R.success(newAddressBook);
    }

    //根据id查询地址
    @GetMapping("/{id}")
    public R get(@PathVariable Long id) {
        log.info("id:{}", id);
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("没有找到该对象");
        }
    }

    //查询默认地址
    @GetMapping("default")
    public R<AddressBook> getDefault() {
        AddressBook defaultAddress = addressBookService.getDefaultAddress();
        if (defaultAddress == null) {
            return R.error("错误，没有找到默认地址！");
        } else {
            log.info("defaultAddress:{}", defaultAddress);
            return R.success(defaultAddress);
        }
    }

    //查询指定用户的全部地址
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook) {
        //设置当前用户id
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);
        List<AddressBook> allAddress = addressBookService.getAllAddress(addressBook);
        return R.success(allAddress);
    }

    //修改地址
    @PutMapping
    public R<String> updateAddressBooke(@RequestBody AddressBook addressBook) {
        log.info("addressBook:{}", addressBook);
        boolean flag = addressBookService.updateAddress(addressBook);
        if (flag) {
            return R.success("修改成功！");
        }
        return R.error("修改失败！");
    }

    //删除地址
    @DeleteMapping
    public R<String> deleteAddressBooke(@RequestParam List<Long> ids) {
        log.info("ids:{}", ids);
        boolean flag = addressBookService.deleteAddress(ids);
        if (flag) {
            return R.success("删除成功！");
        }
        return R.error("删除失败！");
    }

}
