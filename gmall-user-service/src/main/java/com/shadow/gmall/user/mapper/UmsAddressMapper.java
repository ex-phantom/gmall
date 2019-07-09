package com.shadow.gmall.user.mapper;

import com.shadow.gmall.beans.UmsMemberReceiveAddress;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface UmsAddressMapper extends Mapper<UmsMemberReceiveAddress> {
    List<UmsMemberReceiveAddress> getAll();


    UmsMemberReceiveAddress queryUmsMemberById(String id);
}
