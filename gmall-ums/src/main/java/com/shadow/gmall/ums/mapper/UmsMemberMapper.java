package com.shadow.gmall.ums.mapper;

import com.shadow.gmall.beans.UmsMember;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface UmsMemberMapper extends Mapper<UmsMember> {
    List<UmsMember> getAll();


    UmsMember queryUmsAddressById(@Param("id") String id);
}
