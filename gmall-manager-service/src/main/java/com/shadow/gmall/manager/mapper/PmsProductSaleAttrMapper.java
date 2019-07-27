package com.shadow.gmall.manager.mapper;

import com.shadow.gmall.beans.PmsProductSaleAttr;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PmsProductSaleAttrMapper extends Mapper<PmsProductSaleAttr> {
    List<PmsProductSaleAttr> selectspuSaleAttrListWithChecked(@Param("skuId") String skuId,@Param("spuId") String spuId);
}
