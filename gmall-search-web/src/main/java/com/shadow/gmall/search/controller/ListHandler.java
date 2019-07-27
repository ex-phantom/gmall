package com.shadow.gmall.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shadow.gmall.beans.*;
import com.shadow.gmall.service.PmsBaseAttrInfoService;
import com.shadow.gmall.service.PmsBaseAttrValueService;
import com.shadow.gmall.service.PmsSearchSkuInfoServer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
public class ListHandler {
    @Reference
    private PmsSearchSkuInfoServer pmsSearchSkuInfoServer;
    @Reference
    private PmsBaseAttrValueService pmsBaseAttrValueService;
    @Reference
    private PmsBaseAttrInfoService pmsBaseAttrInfoService;

    @RequestMapping("list.html")
    public String goToList(PmsSearchParam pmsSearchParam, Model model)  {
        //从es中查出匹配的对象
        List<PmsSearchSkuInfo> pmsSearchSkuInfoList=this.pmsSearchSkuInfoServer.getSkuInfo(pmsSearchParam);
        model.addAttribute("skuLsInfoList",pmsSearchSkuInfoList);

        //解决页面请求路径的问题
        String currentUrl=this.getPageUrl(pmsSearchParam);
        model.addAttribute("urlParam",currentUrl);

        //查出所有对象中对应的平台属性，和属性值,根据判断是否输入属性值的id，删除该属性值对应的集合中的属性
        List<PmsBaseAttrInfo> pmsBaseAttrInfoList=this.getAllBaseAttrValueBeforeCrumb(pmsSearchSkuInfoList);

        //制作面包屑
        List<PmsSearchCrumb> pmsSearchCrumbList=new ArrayList<>();

        //从pmsBaseAttrInfoList集合中去除含有valueId的PmsBaseAttrInfo
        String[] valueIds = pmsSearchParam.getValueId();
        //判断页面是否通过属性值筛选
        if(valueIds!=null&&valueIds.length>0){
            for (String valueId : valueIds) {
                PmsSearchCrumb pmsSearchCrumb=new PmsSearchCrumb();
                //制作面包屑的url----面包屑中的当前属性值的url上是不包含有自己的属性值id的
                pmsSearchCrumb.setUrlParam(this.getPageUrl(pmsSearchParam,valueId));
                pmsSearchCrumb.setValueId(valueId);

                Iterator<PmsBaseAttrInfo> iterator = pmsBaseAttrInfoList.iterator();
                while (iterator.hasNext()) {
                    List<PmsBaseAttrValue> attrValueList = iterator.next().getAttrValueList();
                    for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {

                        String id = pmsBaseAttrValue.getId();
                        if(valueId.equals(id)){
                            pmsSearchCrumb.setValueName(pmsBaseAttrValue.getValueName());
                            //删除该PmsBaseAttrInfo元素
                            iterator.remove();
                        }
                    }
                }
                //添加到面包屑集合中去
                pmsSearchCrumbList.add(pmsSearchCrumb);
            }
        }
        System.out.println(pmsBaseAttrInfoList);
        model.addAttribute("attrList",pmsBaseAttrInfoList);


        model.addAttribute("attrValueSelectedList",pmsSearchCrumbList);

        return "list";
    }
    //抽出作为公共方法
    private List<PmsBaseAttrInfo> getAllBaseAttrValueBeforeCrumb(List<PmsSearchSkuInfo> pmsSearchSkuInfoList) {
        //创建set集合，对valueId去重
        Set<String> pmsBaseAttrValueIdSet=new HashSet<>();
        //获取pmsSearchSkuInfo对象中对应的pmsBaseAttrValueId加入set集合
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfoList) {
            List<PmsSkuAttrValue> skuAttrValueList = pmsSearchSkuInfo.getSkuAttrValueList();
            for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                pmsBaseAttrValueIdSet.add(pmsSkuAttrValue.getValueId()) ;
            }
        }
        //遍历set集合，查询所有的attrValueId
        String valueIdsStr = StringUtils.join(pmsBaseAttrValueIdSet, ",");
        List<PmsBaseAttrInfo> pmsBaseAttrInfoList=this.pmsBaseAttrInfoService.getAttrInfoListByIds(valueIdsStr);
        return pmsBaseAttrInfoList;
    }
//    制作面包屑
//    private List<PmsSearchCrumb> makeCrumbList(List<PmsBaseAttrInfo> pmsBaseAttrInfoList, PmsSearchParam pmsSearchParam) {
//        List<PmsSearchCrumb> pmsSearchCrumbList=new ArrayList<>();
//        //获取所有的属性值id
//        String[] valueIds = pmsSearchParam.getValueId();
//        if(valueIds!=null&&valueIds.length>0){
//            for (String valueIdForCrumb : valueIds) {
//                PmsSearchCrumb pmsSearchCrumb=new PmsSearchCrumb();
//                //制作面包屑的url----面包屑中的当前属性值的url上是不包含有自己的属性值id的
//                pmsSearchCrumb.setUrlParam(this.getPageUrl(pmsSearchParam,valueIdForCrumb));
//                pmsSearchCrumb.setValueId(valueIdForCrumb);
//                //制作面包屑的名字
//                pmsSearchCrumb.setValueName(this.getCrumbName(pmsBaseAttrInfoList,valueIdForCrumb));
//
//                pmsSearchCrumbList.add(pmsSearchCrumb);
//            }
//
//        }
//        return pmsSearchCrumbList;
//    }


    //解决页面请求路径的问题
    private String getPageUrl(PmsSearchParam pmsSearchParam,String...valueIdForCrumbs ) {
        //根据页面发送的请求，拼接出每个属性或关键字对应的url地址
        String currentUrl="";
        //是否是关键字搜索
        String keyword = pmsSearchParam.getKeyword();
        if(StringUtils.isNotBlank(keyword)){
            currentUrl=currentUrl+"keyword="+keyword;
        }
        //是否是3级分类搜索
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        if(StringUtils.isNotBlank(catalog3Id)){
            if(StringUtils.isNotBlank(currentUrl)){
                currentUrl=currentUrl+"&";
            }
            currentUrl=currentUrl+"catalog3Id="+catalog3Id;
        }
        //拼接平台属性值，点击一个平台属性值，就会制作一个面包屑，url地址栏中才会添加属性值id
        String[] valueIds = pmsSearchParam.getValueId();
        if(valueIds!=null&&valueIds.length>0){
            for (String valueId : valueIds) {
                currentUrl=currentUrl + "&valueId=" + valueId;
                //判断是制作哪个的url
                if(valueIdForCrumbs!=null&&valueIdForCrumbs.length>0){
                    //当面包屑的valueId等于url中要添加的valueId时
                    if(valueId.equals(valueIdForCrumbs[0])){
                        //删除刚添加的valueId
                        int i = currentUrl.lastIndexOf("&");
                        currentUrl=currentUrl.substring(0,i);
                    }

                }

            }
        }
        return currentUrl;
    }


//查出所有对象中对应的平台属性，和属性值，保证页面上的属性都有对应的商品
//    private List<PmsBaseAttrInfo> getAllBaseAttrValue(List<PmsSearchSkuInfo> pmsSearchSkuInfoList,PmsSearchParam pmsSearchParam) {
//
//        List<PmsBaseAttrInfo> pmsBaseAttrInfoList=this.getAllBaseAttrValueBeforeCrumb(pmsSearchSkuInfoList);
//
//        //从pmsBaseAttrInfoList集合中去除含有valueId的PmsBaseAttrInfo
//        String[] valueId = pmsSearchParam.getValueId();
//        //判断页面是否通过属性值筛选
//        if(valueId!=null&&valueId.length>0){
//            List<String> valueIdList = Arrays.asList(valueId);
//            Iterator<PmsBaseAttrInfo> iterator = pmsBaseAttrInfoList.iterator();
//            while (iterator.hasNext()) {
//                List<PmsBaseAttrValue> attrValueList = iterator.next().getAttrValueList();
//                for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
//
//                    String id = pmsBaseAttrValue.getId();
//                    if(valueIdList.contains(id)){
//                        //删除该PmsBaseAttrInfo元素
//                        iterator.remove();
//                    }
//                }
//            }
//        }
//        return pmsBaseAttrInfoList;
//    }




}
