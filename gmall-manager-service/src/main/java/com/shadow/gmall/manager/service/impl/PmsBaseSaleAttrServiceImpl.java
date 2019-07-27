package com.shadow.gmall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shadow.gmall.beans.PmsBaseSaleAttr;
import com.shadow.gmall.manager.mapper.PmsBaseSaleAttrMaper;
import com.shadow.gmall.service.PmsBaseSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class PmsBaseSaleAttrServiceImpl implements PmsBaseSaleAttrService {
    @Autowired
    private PmsBaseSaleAttrMaper pmsBaseSaleAttrMaper;
    @Override
    public List<PmsBaseSaleAttr> getbaseSaleAttrList() {
        List<PmsBaseSaleAttr> pmsBaseSaleAttrList= this.pmsBaseSaleAttrMaper.selectAll();
        return pmsBaseSaleAttrList;
    }
}
