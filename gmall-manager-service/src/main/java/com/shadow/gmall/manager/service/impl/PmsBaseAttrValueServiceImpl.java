package com.shadow.gmall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shadow.gmall.beans.PmsBaseAttrValue;
import com.shadow.gmall.manager.mapper.PmsBaseAttrInfoMappre;
import com.shadow.gmall.manager.mapper.PmsBaseAttrValueMappre;
import com.shadow.gmall.service.PmsBaseAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class PmsBaseAttrValueServiceImpl implements PmsBaseAttrValueService {

    @Autowired
    private PmsBaseAttrInfoMappre pmsBaseAttrInfoMappre;
    @Autowired
    private PmsBaseAttrValueMappre pmsBaseAttrValueMappre;

    @Override
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {
        PmsBaseAttrValue pmsBaseAttrValue=new PmsBaseAttrValue();
        pmsBaseAttrValue.setAttrId(attrId);
        List<PmsBaseAttrValue> pmsBaseAttrValueList=this.pmsBaseAttrValueMappre.select(pmsBaseAttrValue);
        return pmsBaseAttrValueList;
    }
}
