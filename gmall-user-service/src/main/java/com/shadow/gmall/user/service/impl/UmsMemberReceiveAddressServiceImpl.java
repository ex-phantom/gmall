package com.shadow.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shadow.gmall.beans.UmsMemberReceiveAddress;
import com.shadow.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.shadow.gmall.service.UmsMemberReceiveAddressService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Service
public class UmsMemberReceiveAddressServiceImpl implements UmsMemberReceiveAddressService {
    @Autowired
    private UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;


    @Override
    public List<UmsMemberReceiveAddress> getUserAddressByMemberId(String memberId) {
        UmsMemberReceiveAddress umsMemberReceiveAddress=new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setMemberId(memberId);
        List<UmsMemberReceiveAddress> select = this.umsMemberReceiveAddressMapper.select(umsMemberReceiveAddress);
        return select;
    }

    @Override
    public UmsMemberReceiveAddress getUserAddressById(String userAddressId) {
        UmsMemberReceiveAddress umsMemberReceiveAddress = this.umsMemberReceiveAddressMapper.selectByPrimaryKey(userAddressId);
        return umsMemberReceiveAddress;
    }
}
