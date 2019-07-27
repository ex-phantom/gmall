package com.shadow.gmall.service;

import com.shadow.gmall.beans.PmsProductInfo;
import com.shadow.gmall.beans.PmsSkuInfo;

import java.util.List;

public interface PmsProductInfoService {
    List<PmsProductInfo> getspuList(String catalog3Id);

    void saveSpuInfo(PmsProductInfo pmsProductInfo);


}
