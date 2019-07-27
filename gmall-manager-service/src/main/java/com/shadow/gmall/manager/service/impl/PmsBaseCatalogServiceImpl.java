package com.shadow.gmall.manager.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.shadow.gmall.beans.PmsBaseCatalog1;
import com.shadow.gmall.beans.PmsBaseCatalog2;
import com.shadow.gmall.beans.PmsBaseCatalog3;
import com.shadow.gmall.manager.mapper.PmsBaseCatalog1Mapper;
import com.shadow.gmall.manager.mapper.PmsBaseCatalog2Mapper;
import com.shadow.gmall.manager.mapper.PmsBaseCatalog3Mapper;
import com.shadow.gmall.service.PmsBaseCatalogService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Service
public class PmsBaseCatalogServiceImpl implements PmsBaseCatalogService {
    @Autowired
    private PmsBaseCatalog1Mapper pmsBaseCatalog1Mapper;
    @Autowired
    private PmsBaseCatalog2Mapper pmsBaseCatalog2Mapper;
    @Autowired
    private PmsBaseCatalog3Mapper pmsBaseCatalog3Mapper;
    @Override
    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id) {
        PmsBaseCatalog3 pmsBaseCatalog3=new PmsBaseCatalog3();
        pmsBaseCatalog3.setCatalog2Id(catalog2Id);
        return this.pmsBaseCatalog3Mapper.select(pmsBaseCatalog3);
    }
    @Override
    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id) {
        PmsBaseCatalog2 pmsBaseCatalog2=new PmsBaseCatalog2();
        pmsBaseCatalog2.setCatalog1Id(catalog1Id);
        return this.pmsBaseCatalog2Mapper.select(pmsBaseCatalog2);

    }
    @Override
    public List<PmsBaseCatalog1> getCatalog1() {
        return this.pmsBaseCatalog1Mapper.selectAll();

    }
}
