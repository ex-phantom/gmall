package com.shadow.gmall.service;

import com.shadow.gmall.beans.PmsBaseAttrInfo;

import java.util.List;

public interface PmsBaseAttrInfoService {
    List<PmsBaseAttrInfo> getattrInfoList(String catalog3Id);

    void savaAttrInfo( PmsBaseAttrInfo pmsBaseAttrInfo );

    List<PmsBaseAttrInfo> getAttrInfoListByIds(String valueIdsStr);
}
