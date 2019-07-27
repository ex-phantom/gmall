package com.shadow.gmall.service;

import com.shadow.gmall.beans.PmsProductSaleAttr;

import java.util.List;

public interface PmsProductSaleAttrService {
    List<PmsProductSaleAttr> getspuSaleAttrList(String spuId);

    List<PmsProductSaleAttr> getspuSaleAttrListWithChecked(String skuId, String spuId);
}
