package com.shadow.gmall.manager.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shadow.gmall.beans.PmsBaseCatalog1;
import com.shadow.gmall.beans.PmsBaseCatalog2;
import com.shadow.gmall.beans.PmsBaseCatalog3;
import com.shadow.gmall.service.PmsBaseCatalogService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class CatalogHandler {
    @Reference
    private PmsBaseCatalogService pmsBaseCatalogService;

    @RequestMapping("index")
    public String index(){
        return "hello world!!!";
    }

    @RequestMapping("getCatalog1")
    public List<PmsBaseCatalog1> getCatalog1(){
        List<PmsBaseCatalog1> pmsBaseCatalog1List=this.pmsBaseCatalogService.getCatalog1();
        return pmsBaseCatalog1List;
    }

    @RequestMapping("getCatalog2")
    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id){
        List<PmsBaseCatalog2> pmsBaseCatalog2List=this.pmsBaseCatalogService.getCatalog2(catalog1Id);
        return pmsBaseCatalog2List;
    }
    @RequestMapping("getCatalog3")
    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id){
        List<PmsBaseCatalog3> pmsBaseCatalog3List=this.pmsBaseCatalogService.getCatalog3(catalog2Id);
        return pmsBaseCatalog3List;
    }

}
