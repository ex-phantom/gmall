package com.shadow.gmall.service;

import com.shadow.gmall.beans.PmsSearchParam;
import com.shadow.gmall.beans.PmsSearchSkuInfo;

import java.util.List;

public interface PmsSearchSkuInfoServer {


    List<PmsSearchSkuInfo> getSkuInfo(PmsSearchParam pmsSearchParam);
}
