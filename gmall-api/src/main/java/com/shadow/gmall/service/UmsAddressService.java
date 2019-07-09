package com.shadow.gmall.service;

import com.shadow.gmall.beans.UmsMemberReceiveAddress;

import java.util.List;

public interface UmsAddressService {
    List<UmsMemberReceiveAddress> getAll();

    UmsMemberReceiveAddress getUmsById(String id);


    Integer delUmsById(String id);

    Integer updataUms(UmsMemberReceiveAddress umsMemberReceiveAddress);

    UmsMemberReceiveAddress queryUmsMemberById(String id);

}
