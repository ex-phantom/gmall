package com.shadow.gmall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shadow.gmall.beans.PmsProductImage;
import com.shadow.gmall.beans.PmsProductInfo;
import com.shadow.gmall.beans.PmsProductSaleAttr;
import com.shadow.gmall.beans.PmsProductSaleAttrValue;
import com.shadow.gmall.manager.mapper.PmsProductImageMapper;
import com.shadow.gmall.manager.mapper.PmsProductInfoMapper;
import com.shadow.gmall.manager.mapper.PmsProductSaleAttrMapper;
import com.shadow.gmall.manager.mapper.PmsProductSaleAttrValueMapper;
import com.shadow.gmall.service.PmsProductInfoService;
import com.shadow.gmall.service.PmsProductInfoService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class PmsProductInfoServiceImpl implements PmsProductInfoService {
    @Autowired
    private PmsProductInfoMapper pmsProductInfoMapper;
    @Autowired
    private PmsProductImageMapper pmsProductImageMapper;
    @Autowired
    private PmsProductSaleAttrMapper pmsProductSaleAttrMapper;
    @Autowired
    private PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;

    @Override
    public List<PmsProductInfo> getspuList(String catalog3Id) {
        PmsProductInfo pmsProductInfo=new PmsProductInfo();
        pmsProductInfo.setCatalog3Id(catalog3Id);
        List<PmsProductInfo> pmsProductInfoList = this.pmsProductInfoMapper.select(pmsProductInfo);
        return pmsProductInfoList;
    }

    @Override
    public void saveSpuInfo(PmsProductInfo pmsProductInfo) {
        //将添加的spu的信息插入数据库
        this.pmsProductInfoMapper.insertSelective(pmsProductInfo);
        //获取自动生成主键
        String pmsProductInfoId=pmsProductInfo.getId();
        //获取上传的多张图片，插入数据库
        List<PmsProductImage> pmsProductImage=pmsProductInfo.getSpuImageList();
        for (PmsProductImage productImage : pmsProductImage) {
            //存入与pmsProductInfo有关的Id信息
            productImage.setProductId(pmsProductInfoId);
            this.pmsProductImageMapper.insertSelective(productImage);
        }
        //获取上传的销售属性
        List<PmsProductSaleAttr> pmsProductSaleAttrList=pmsProductInfo.getSpuSaleAttrList();
        for (PmsProductSaleAttr pmsProductSaleAttr : pmsProductSaleAttrList) {
            //存入与pmsProductInfo有关的Id信息
            pmsProductSaleAttr.setProductId(pmsProductInfoId);
            //将销售属性插入数据库
            this.pmsProductSaleAttrMapper.insertSelective(pmsProductSaleAttr);
            //获取销售属性的值
            List<PmsProductSaleAttrValue> pmsProductSaleAttrValueList=pmsProductSaleAttr.getSpuSaleAttrValueList();
            for (PmsProductSaleAttrValue pmsProductSaleAttrValue : pmsProductSaleAttrValueList) {
                //存入与pmsProductInfo有关的Id信息
                pmsProductSaleAttrValue.setProductId(pmsProductInfoId);
                //将销售属性值插入数据库
                this.pmsProductSaleAttrValueMapper.insertSelective(pmsProductSaleAttrValue);
            }
        }
    }
}
