package com.shadow.gmall.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.shadow.gmall.beans.*;
import com.shadow.gmall.myAnnotation.SSOAnnotation;
import com.shadow.gmall.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class IndexHandler {

    @Reference
    private PmsBaseCatalogService pmsBaseCatalogService;

    @SSOAnnotation
    @RequestMapping("index")
    public String getIndex(){
        return "index";
    }


    //查询三级分类属性
    public void getCatalogs() throws IOException {
        //获取页面三级分类
        List<PmsBaseCatalog1> catalog1 = this.pmsBaseCatalogService.getCatalog1();
        Map<String,List<PmsBaseCatalog2>> map=new HashMap<>();

        for (PmsBaseCatalog1 pmsBaseCatalog1 : catalog1) {

            List<PmsBaseCatalog2> catalog2 = this.pmsBaseCatalogService.getCatalog2(pmsBaseCatalog1.getId());
            for (PmsBaseCatalog2 pmsBaseCatalog2 : catalog2) {
                List<PmsBaseCatalog3> catalog3 = this.pmsBaseCatalogService.getCatalog3(pmsBaseCatalog2.getId());
                pmsBaseCatalog2.setCatalog3List(catalog3);
            }
            pmsBaseCatalog1.setCatalog2List(catalog2);

            map.put(pmsBaseCatalog1.getId(),catalog2);
        }
        //写入到某一文件中
        String catalog1JsonStr= JSON.toJSONString(map);
        FileOutputStream file=new FileOutputStream(new File("D:\\Git\\gmall\\gmall-search-web\\src\\main\\resources\\static\\index\\json\\catalog1.json"));
        file.write(catalog1JsonStr.getBytes());
        file.close();
    }


}
