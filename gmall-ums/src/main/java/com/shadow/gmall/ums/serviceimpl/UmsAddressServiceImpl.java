package com.shadow.gmall.ums.serviceimpl;

//import com.alibaba.dubbo.config.annotation.Service;
import com.shadow.gmall.beans.UmsMemberReceiveAddress;
import com.shadow.gmall.service.UmsAddressService;
import com.shadow.gmall.ums.mapper.UmsAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UmsAddressServiceImpl implements UmsAddressService {
    @Autowired
    private UmsAddressMapper umsAddressMapper;

    @Override
    public List<UmsMemberReceiveAddress> getAll() {
        return this.umsAddressMapper.getAll();
    }
    //使用通用mapper后，表名要和类名一一对应，满足驼峰匹配
    @Override
    public UmsMemberReceiveAddress getUmsById(String id) {
        UmsMemberReceiveAddress umsMemberReceiveAddress1 = this.umsAddressMapper.selectByPrimaryKey(id);
        return umsMemberReceiveAddress1;
    }
    //使用通用mapper后，表名要和类名一一对应，满足驼峰匹配
    @Override
    public Integer delUmsById(String id) {
       int i= this.umsAddressMapper.deleteByPrimaryKey(id);
        return i;
    }
    //使用通用mapper后，表名要和类名一一对应，满足驼峰匹配
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
