package com.hive.takeout.controller;

import com.hive.takeout.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 进行文件的下载和上传
 * */
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {
    @Value("${takeoutHive.path}")
    private String basePath;

    @PostMapping("/upload")
    //文件的上传
    public R<String> upload(MultipartFile file){
        log.info("文件的上传功能"+basePath);

        //得到文件的初始名称
        String originalName = file.getOriginalFilename();

        //得到文件的后缀名称
        String suffix = originalName.substring(originalName.lastIndexOf("."));

        //修改文件的初始名称，改成UUID为前缀的图片，防止发生重复的图片
        String fileName = UUID.randomUUID().toString().replaceAll("-","")+suffix;

        //判断当前服务器是否有images目录，如果没有就进行创建
        File dir = new File(basePath);
        if (!dir.exists()){
            dir.mkdir();
        }
        try {
            //将文件存储到D:\takeoutHive\src\main\resources\images
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    //文件的下载
    @GetMapping("download")
    public void download(HttpServletResponse response,String name){
        log.info("文件的下载");
        //文件输入流
        FileInputStream inputStream = null;
        //文件输出流
        ServletOutputStream outputStream = null;
        try {
            //将文件读入到输入流中
            inputStream = new FileInputStream(new File(basePath+name));

            //得到文件的输出流
           outputStream = response.getOutputStream();

            //将文件输出响应到浏览器端
            byte[] bytes = new byte[1024];
            int length = 0;
            while ((length=inputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
