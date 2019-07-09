package com.shadow.gmall.service;

import com.shadow.gmall.beans.UmsMember;
import java.util.List;

public interface UmsMemberService {
    List<UmsMember> getAll();

    UmsMember getUmsById(String id);


    Integer delUmsById(String id);

    Integer updataUms(UmsMember umsMember);

    UmsMember queryUmsAddressById(String id);
}
