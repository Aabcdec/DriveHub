package com.example.web.Controller;

import com.example.web.util.AliOSSUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
public class AliOSSController {
    @Resource
    private AliOSSUtils aliOSSUtils;
    //文件上传
    @PostMapping("/upload")
    public String upload (@RequestParam("file") MultipartFile file) throws Exception {
        System.out.println(file);
        String upload = aliOSSUtils.upload(file);
        return upload;
    }
}
