package com.shadow.gmall.manager.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shadow.gmall.beans.PmsSkuInfo;
import com.shadow.gmall.service.PmsSkuInfoService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class SkuHandler {
    @Reference
    private PmsSkuInfoService pmsSkuInfoService;

    @RequestMapping("saveSkuInfo")
    public String saveSkuInfo(@RequestBody PmsSkuInfo pmsSkuInfo){
        this.pmsSkuInfoService.saveSkuInfo(pmsSkuInfo);
        return "success";
    }

}
