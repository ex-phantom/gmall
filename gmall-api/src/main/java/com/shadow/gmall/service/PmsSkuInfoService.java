package com.shadow.gmall.service;

import com.shadow.gmall.beans.PmsSkuInfo;

import java.util.List;

public interface PmsSkuInfoService {
    void saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    PmsSkuInfo queryByIdFromRedisExample(String skuId);

    PmsSkuInfo queryByIdFromRedis(String skuId);

    PmsSkuInfo queryById(String skuId);

    List<PmsSkuInfo> queryByProductId(String spuId);

    List<PmsSkuInfo> selectAll();
}
