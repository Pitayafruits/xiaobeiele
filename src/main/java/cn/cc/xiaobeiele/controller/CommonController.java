package cn.cc.xiaobeiele.controller;

import cn.cc.xiaobeiele.common.R;
import cn.cc.xiaobeiele.utils.QiniuUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.UUID;

/**
 *文件上传和下载
 */

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    //文件上传
    @PostMapping("/upload")
    public R<String> uploadPic(MultipartFile file){
        log.info(file.toString());
        //获得上传文件的原始名并截取后缀
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //通过UUID生成新的文件名
        String picNane = UUID.randomUUID().toString() + suffix;
        //通过七牛云上传图片
        try {
            QiniuUtils.uploadFileQiniu(file.getBytes(),picNane);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(picNane);
    }

}
