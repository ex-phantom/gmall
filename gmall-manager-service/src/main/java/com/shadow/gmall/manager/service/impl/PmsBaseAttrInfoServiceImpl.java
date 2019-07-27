package com.shadow.gmall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shadow.gmall.beans.PmsBaseAttrInfo;
import com.shadow.gmall.beans.PmsBaseAttrValue;
import com.shadow.gmall.manager.mapper.PmsBaseAttrInfoMappre;
import com.shadow.gmall.manager.mapper.PmsBaseAttrValueMappre;
import com.shadow.gmall.service.PmsBaseAttrInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Service
public class PmsBaseAttrInfoServiceImpl implements PmsBaseAttrInfoService {

    @Autowired
    private PmsBaseAttrInfoMappre pmsBaseAttrInfoMappre;
    @Autowired
    private PmsBaseAttrValueMappre pmsBaseAttrValueMappre;

    @Override
    public List<PmsBaseAttrInfo> getattrInfoList(String catalog3Id) {
        PmsBaseAttrInfo pmsBaseAttrInfo=new PmsBaseAttrInfo();
        pmsBaseAttrInfo.setCatalog3Id(catalog3Id);

        List<PmsBaseAttrInfo> pmsBaseAttrInfoList=this.pmsBaseAttrInfoMappre.select(pmsBaseAttrInfo);

        for (PmsBaseAttrInfo baseAttrInfo : pmsBaseAttrInfoList) {
            //获取PmsBaseAttrValue查询条件
            PmsBaseAttrValue PmsBaseAttrValue=new PmsBaseAttrValue();
            PmsBaseAttrValue.setAttrId(baseAttrInfo.getId());
            //查询对应的平台属性
            List<PmsBaseAttrValue> select = this.pmsBaseAttrValueMappre.select(PmsBaseAttrValue);
            //封装
            baseAttrInfo.setAttrValueList(select);
        }
        return pmsBaseAttrInfoList;
    }

    @Override
    public void savaAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {
        //获取传输来的封装的PmsBaseAttrInfo对象的id
        String attrIdOld = pmsBaseAttrInfo.getId();
        //获取传输的pmsBaseAttrValueList
        List<PmsBaseAttrValue> pmsBaseAttrValueList=pmsBaseAttrInfo.getAttrValueList();
        //判断操作是修改还是保存
        if(StringUtils.isBlank(attrIdOld)){
            //保存，插入
            this.pmsBaseAttrInfoMappre.insertSelective(pmsBaseAttrInfo);
            //获取自增的主键
            String attrIdNew = pmsBaseAttrInfo.getId();
            //遍历插入
            for (PmsBaseAttrValue ps:pmsBaseAttrValueList) {
                ps.setAttrId(attrIdNew);
                this.pmsBaseAttrValueMappre.insertSelective(ps);
            }
        }else {
            //根据id修改属性名称
            this.pmsBaseAttrInfoMappre.updateByPrimaryKeySelective(pmsBaseAttrInfo);
            //修改，删除原有的
            PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
            pmsBaseAttrValue.setAttrId(attrIdOld);
            this.pmsBaseAttrValueMappre.delete(pmsBaseAttrValue);
            //遍历插入
            for (PmsBaseAttrValue ps:pmsBaseAttrValueList) {
                //修改中增加PmsBaseAttrValue属性，传输来的PmsBaseAttrValue没有attrId
                ps.setAttrId(attrIdOld);
                this.pmsBaseAttrValueMappre.insertSelective(ps);
            }
        }
    }

    @Override
    public List<PmsBaseAttrInfo> getAttrInfoListByIds(String valueIdsStr) {
        //根据id查询PmsBaseAttrInfo,查询pmsBaseAttrValue
        List<PmsBaseAttrInfo> pmsBaseAttrInfoList=this.pmsBaseAttrInfoMappre.selectAttrInfoListByIds(valueIdsStr);
        return pmsBaseAttrInfoList;
    }
}
