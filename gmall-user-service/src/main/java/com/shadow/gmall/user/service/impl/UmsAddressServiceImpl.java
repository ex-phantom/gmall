package com.shadow.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shadow.gmall.beans.UmsMemberReceiveAddress;
import com.shadow.gmall.user.mapper.UmsAddressMapper;
import com.shadow.gmall.service.UmsAddressService;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

@Service
public class UmsAddressServiceImpl implements UmsAddressService {
    @Autowired
    private UmsAddressMapper umsAddressMapper;

    @Override
    public List<UmsMemberReceiveAddress> getAll() {
        return this.umsAddressMapper.getAll();
    }

    @Override
    public UmsMemberReceiveAddress getUmsById(String id) {
        UmsMemberReceiveAddress umsMemberReceiveAddress =new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setId(id);
        UmsMemberReceiveAddress umsMemberReceiveAddress1 = this.umsAddressMapper.selectOne(umsMemberReceiveAddress);
        return umsMemberReceiveAddress1;
    }

    @Override
    public Integer delUmsById(String id) {
       int i= this.umsAddressMapper.deleteByPrimaryKey(id);
        return i;
    }

    @Override
    public Integer updataUms(UmsMemberReceiveAddress umsMemberReceiveAddress) {
        Integer i= this.umsAddressMapper.updateByPrimaryKeySelective(umsMemberReceiveAddress);
        return i;
    }

    @Override
    public UmsMemberReceiveAddress queryUmsMemberById(String id) {

        UmsMemberReceiveAddress umsaddress=this.umsAddressMapper.queryUmsMemberById(id);
        return umsaddress;

    }
}
