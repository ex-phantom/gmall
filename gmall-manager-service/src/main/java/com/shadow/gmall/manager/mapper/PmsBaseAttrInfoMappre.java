package com.shadow.gmall.manager.mapper;

import com.shadow.gmall.beans.PmsBaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PmsBaseAttrInfoMappre extends Mapper<PmsBaseAttrInfo>{
    List<PmsBaseAttrInfo> selectAttrInfoListByIds(@Param("valueIdsStr") String valueIdsStr);
}
