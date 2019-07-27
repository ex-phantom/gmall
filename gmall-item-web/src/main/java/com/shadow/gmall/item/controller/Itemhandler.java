package com.shadow.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.shadow.gmall.beans.PmsProductSaleAttr;
import com.shadow.gmall.beans.PmsProductSaleAttrValue;
import com.shadow.gmall.beans.PmsSkuInfo;
import com.shadow.gmall.beans.PmsSkuSaleAttrValue;
import com.shadow.gmall.service.PmsProductSaleAttrService;
import com.shadow.gmall.service.PmsSkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class Itemhandler {

    @Reference
    private PmsSkuInfoService pmsSkuInfoService;

    @Reference
    private PmsProductSaleAttrService pmsProductSaleAttrService;


    @RequestMapping("{id}.html")
    public String pageIndex(@PathVariable("id") String skuId, Model model) {
        //根据skuId查询具体的sku商品信息
        //PmsSkuInfo pmsSkuInfo = this.pmsSkuInfoService.queryByIdFromRedis(skuId);
        PmsSkuInfo pmsSkuInfo = this.pmsSkuInfoService.queryByIdFromRedis(skuId);
        //根据skuId获得具体的销售属性值
        model.addAttribute("skuInfo", pmsSkuInfo);

        //做销售属性的展示功能
        //获取该类商品所有的销售属性和属性值，通过spuId,同时定位该商品的属性值
          //String spuId = pmsSkuInfo.getProductId();
          //List<PmsProductSaleAttr> pmsProductSaleAttrList = this.pmsProductSaleAttrService.getspuSaleAttrList(skuId);
        //定位该商品的销售属性在所有的销售属性和属性值中的位置。
         String spuId = pmsSkuInfo.getProductId();
          List<PmsProductSaleAttr> pmsProductSaleAttrList = this.pmsProductSaleAttrService.getspuSaleAttrListWithChecked(skuId,spuId);
        model.addAttribute("spuSaleAttrListCheckBySku", pmsProductSaleAttrList);


        //形成hash表放入页面，准备页面通过属性进行跳转
        //获取该类商品下的所有所有商品的skuid，根据skuid查询每个商品对应的销售属性saleAttrid和属性值SaleAttrValueid
        List<PmsSkuInfo> pmsSkuInfoList = this.pmsSkuInfoService.queryByProductId(spuId);
        //将所有的SaleAttrValue作为key,skuId作为value形成hash表放入页面
        Map<String, String> skuSaleAttrValueMap = new HashMap();
        for (PmsSkuInfo skuInfo : pmsSkuInfoList) {
            // List<String> skuSaleAttrValueList=new ArrayList();
            String skuSaleAttrValueStr = "|";
            //获取销售属性值的集合
            List<PmsSkuSaleAttrValue> skuSaleAttrValueList1 = skuInfo.getSkuSaleAttrValueList();
            //遍历得到销售属性值的id
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList1) {
                // skuSaleAttrValueList.add(pmsSkuSaleAttrValue.getSaleAttrValueId());
                skuSaleAttrValueStr = skuSaleAttrValueStr + pmsSkuSaleAttrValue.getSaleAttrValueId() + "|";
            }
            skuSaleAttrValueMap.put(skuSaleAttrValueStr, skuInfo.getId());
        }
        model.addAttribute("skuSaleAttrValueMap", JSON.toJSONString(skuSaleAttrValueMap));
        return "item";
    }
}
