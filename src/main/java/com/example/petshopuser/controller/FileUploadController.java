package com.example.petshopuser.controller;

import com.example.petshopuser.common.Constants;
import com.example.petshopuser.entity.ReturnObj;
import com.example.petshopuser.utils.SnowflakeIdWorker;
import com.example.petshopuser.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;

@RestController
@RequestMapping("/file")
public class FileUploadController {

    private final SnowflakeIdWorker snowflakeIdWorker;

    @Autowired
    public FileUploadController(SnowflakeIdWorker snowflakeIdWorker) {
        this.snowflakeIdWorker = snowflakeIdWorker;
    }

    // 仅仅作为一个上传文件的接口
    @PostMapping("/upload")
    private ReturnObj upload(MultipartFile image){
        ReturnObj returnObj = new ReturnObj();
        // 检查是否接收到文件
        if(image==null){
            returnObj.setCode(Constants.CODE_400);
            returnObj.setMsg("param name error.");
            return returnObj;
        }
        if (image.isEmpty()) {
            returnObj.setCode(Constants.CODE_400);
            returnObj.setMsg("No file received.");
            return returnObj;
        }
        // 检查文件类型
        String contentType = image.getContentType();
        if (!contentType.startsWith("image/")) {
            returnObj.setCode(Constants.CODE_400);
            returnObj.setMsg("Invalid file type. Only image files are allowed.");
            return returnObj;
        }

        String uploadUrl = "http://124.70.51.6:8000/upload/";
        String staticImagePath = "E:\\作业文件\\实训\\code\\petShopUser\\src\\main\\resources\\static\\";

        String filename = savefile(image);
        if(filename==null){
            returnObj.setCode(Constants.CODE_500);
            returnObj.setMsg("failed");
            return returnObj;
        }

        String imagePath = staticImagePath+filename;
        String code = Utils.sendImageToDjango(imagePath, uploadUrl);
        if(code.equals(Constants.CODE_200)){
            HashMap<String, String> file = new HashMap<>();
            file.put("filename",filename);
            returnObj.setData(file);
            returnObj.setCode(Constants.CODE_200);
            returnObj.setMsg("success");
        }else{
            returnObj.setCode(Constants.CODE_500);
            returnObj.setMsg("failed");
        }
        return returnObj;
    }

    private String savefile(MultipartFile image) {
        String filename = null;
        try {
            String savePath = "E:\\作业文件\\实训\\code\\petShopUser\\src\\main\\resources\\static"; // 设置保存路径
            String imageName = image.getOriginalFilename();
            System.out.println(imageName);
            int end_name_idx = imageName.lastIndexOf(".");
            String extension = imageName.substring(end_name_idx);
            filename = String.valueOf(snowflakeIdWorker.nextId())+extension;
            File destFile = new File(savePath , filename);
            image.transferTo(destFile);

        } catch (Exception e) {
            // 处理文件保存失败的逻辑
            e.printStackTrace();
            return null;
        }

        return filename;
    }

}
