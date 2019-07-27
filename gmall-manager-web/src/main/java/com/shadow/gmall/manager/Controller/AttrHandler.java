package com.shadow.gmall.manager.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shadow.gmall.beans.PmsBaseAttrInfo;
import com.shadow.gmall.beans.PmsBaseAttrValue;
import com.shadow.gmall.beans.PmsBaseSaleAttr;
import com.shadow.gmall.service.PmsBaseAttrInfoService;
import com.shadow.gmall.service.PmsBaseAttrValueService;
import com.shadow.gmall.service.PmsBaseSaleAttrService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class AttrHandler {
    @Reference
    private PmsBaseAttrInfoService pmsBaseAttrInfoService;
    @Reference
    private PmsBaseAttrValueService pmsBaseAttrValueService;
    @Reference
    private PmsBaseSaleAttrService pmsBaseSaleAttrService;

    @RequestMapping("attrInfoList")
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id){
        List<PmsBaseAttrInfo> pmsBaseAttrInfoList=this.pmsBaseAttrInfoService.getattrInfoList(catalog3Id);
        return pmsBaseAttrInfoList;
    }

    @RequestMapping("getAttrValueList")
    public List<PmsBaseAttrValue> getAttrValueList(String attrId){
        List<PmsBaseAttrValue> pmsBaseAttrValueList=this.pmsBaseAttrValueService.getAttrValueList(attrId);
        return pmsBaseAttrValueList;
    }

    @RequestMapping("saveAttrInfo")
    public String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo ){
        this.pmsBaseAttrInfoService.savaAttrInfo(pmsBaseAttrInfo);
        return "success";
    }
    @RequestMapping("baseSaleAttrList")
    public List<PmsBaseSaleAttr> baseSaleAttrList(){
        List<PmsBaseSaleAttr> pmsBaseSaleAttrList=this.pmsBaseSaleAttrService.getbaseSaleAttrList();
        return pmsBaseSaleAttrList;
    }

}
