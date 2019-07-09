package com.shadow.gmall.user.mapper;

import com.shadow.gmall.beans.UmsMember;
import org.apache.ibatis.annotations.Param;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.mapper.common.Mapper;


import java.util.List;

public interface UmsMemberMapper extends Mapper<UmsMember> {
    List<UmsMember> getAll();


    UmsMember queryUmsAddressById(@Param("id") String id);
}
