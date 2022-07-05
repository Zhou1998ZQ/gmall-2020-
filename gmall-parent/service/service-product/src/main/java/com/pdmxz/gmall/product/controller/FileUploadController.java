package com.pdmxz.gmall.product.controller;


import com.pdmxz.gmall.common.result.Result;
import com.pdmxz.gmall.product.utils.FastDFSUtils;
import org.apache.commons.io.FilenameUtils;
import org.csource.common.MyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/admin/product")
public class FileUploadController {

    @Value("${img.url}")
    private String fileUrl; // http://192.168.67.224



    @RequestMapping("fileUpload")
    public Result fileUpload(MultipartFile file) throws IOException, MyException {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String fileId = FastDFSUtils.upload(file.getBytes(), extension);


        return Result.ok(fileUrl+fileId);
    }
}
