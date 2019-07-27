package com.shadow.gmall.service;

import com.shadow.gmall.beans.UmsMemberReceiveAddress;

import java.util.List;

public interface UmsMemberReceiveAddressService {
    List<UmsMemberReceiveAddress> getUserAddressByMemberId(String memberId);

    UmsMemberReceiveAddress getUserAddressById(String userAddressId);
}
