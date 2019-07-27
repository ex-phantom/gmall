package com.shadow.gmall.manager.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shadow.gmall.beans.PmsProductImage;
import com.shadow.gmall.beans.PmsProductInfo;
import com.shadow.gmall.beans.PmsProductSaleAttr;
import com.shadow.gmall.service.PmsProductImageService;
import com.shadow.gmall.service.PmsProductInfoService;
import com.shadow.gmall.service.PmsProductInfoService;
import com.shadow.gmall.service.PmsProductSaleAttrService;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
public class SpuHandler {
    @Reference
    private PmsProductInfoService PmsProductInfoService;

    @Reference
    private PmsProductSaleAttrService pmsProductSaleAttrService;

    @Reference
    private PmsProductImageService pmsProductImageService;

    @RequestMapping("spuList")
    public List<PmsProductInfo> getspuList(String catalog3Id){
        List<PmsProductInfo> pmsProductInfoList= this.PmsProductInfoService.getspuList(catalog3Id);
        return pmsProductInfoList;
    }
    @RequestMapping("fileUpload")
    public String getFileUpload(@RequestParam("file") MultipartFile multipartFile){
        String str="http://192.168.78.32";
        //获取tracker连接
        //获取tracker配置文件的路径D:\Git\gmall\gmall-manager-web\src\main\resources\tracker.conf
        String path = SpuHandler.class.getResource("/tracker.conf").getPath();
        //初始化tracker
        try {
            ClientGlobal.init(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //创建tracker对象
        TrackerClient trackerClient = new TrackerClient();
        //获取tracker的连接
        TrackerServer connection = null;
        try {
            connection = trackerClient.getConnection();
            //获取输入图片的二进制字节流
            byte[] bytes = multipartFile.getBytes();
            //通过tracker获得storage
            StorageClient storageClient = new StorageClient(connection, null);

            //通过storage上传文件
            String[] jpgs = storageClient.upload_appender_file(bytes, "jpg", null);

            for (String jpg : jpgs) {
                str +="/"+jpg;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return str;
    }

    @RequestMapping("saveSpuInfo")
    public String saveSpuInfo(@RequestBody  PmsProductInfo pmsProductInfo){
        this.PmsProductInfoService.saveSpuInfo(pmsProductInfo);
        return "success";
    }


   @RequestMapping("spuSaleAttrList")
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId){
        List<PmsProductSaleAttr> pmsProductSaleAttrList=this.pmsProductSaleAttrService.getspuSaleAttrList(spuId);
        return pmsProductSaleAttrList;
    }

    @RequestMapping("spuImageList")
    public List<PmsProductImage> spuImageList(String spuId){
        List<PmsProductImage> pmsProductImageList= this.pmsProductImageService.getspuImageList(spuId);
        return pmsProductImageList;
    }
}
