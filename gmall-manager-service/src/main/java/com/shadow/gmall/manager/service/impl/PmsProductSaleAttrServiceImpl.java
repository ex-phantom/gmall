package com.shadow.gmall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shadow.gmall.beans.PmsProductSaleAttr;
import com.shadow.gmall.beans.PmsProductSaleAttrValue;
import com.shadow.gmall.manager.mapper.PmsProductSaleAttrMapper;
import com.shadow.gmall.manager.mapper.PmsProductSaleAttrValueMapper;
import com.shadow.gmall.service.PmsProductSaleAttrService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Service
public class PmsProductSaleAttrServiceImpl implements PmsProductSaleAttrService {
    @Autowired
    private PmsProductSaleAttrMapper pmsProductSaleAttrMapper;
    @Autowired
    private PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;
    @Override
    public List<PmsProductSaleAttr> getspuSaleAttrList(String spuId) {
        //定义查询条件
        PmsProductSaleAttr pmsProductSaleAttr=new PmsProductSaleAttr();
        pmsProductSaleAttr.setProductId(spuId);
        //查询所有销售属性
        List<PmsProductSaleAttr> select = this.pmsProductSaleAttrMapper.select(pmsProductSaleAttr);
        for (PmsProductSaleAttr productSaleAttr : select) {
            //定义查询条件
            PmsProductSaleAttrValue pmsProductSaleAttrValue=new PmsProductSaleAttrValue();
            pmsProductSaleAttrValue.setProductId(spuId);
            pmsProductSaleAttrValue.setSaleAttrId(productSaleAttr.getSaleAttrId());
            //查询所有销售属性值
            List<PmsProductSaleAttrValue> select1 = this.pmsProductSaleAttrValueMapper.select(pmsProductSaleAttrValue);
            //封装
            productSaleAttr.setSpuSaleAttrValueList(select1);
        }
        return select;
    }

    @Override
    public List<PmsProductSaleAttr> getspuSaleAttrListWithChecked(String skuId, String spuId) {
        List<PmsProductSaleAttr> pmsProductSaleAttrList= this.pmsProductSaleAttrMapper.selectspuSaleAttrListWithChecked( skuId,spuId);

        return pmsProductSaleAttrList;
    }
}
