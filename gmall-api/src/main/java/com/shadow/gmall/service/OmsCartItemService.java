package com.shadow.gmall.service;

import com.shadow.gmall.beans.OmsCartItem;

import java.math.BigDecimal;
import java.util.List;

public interface OmsCartItemService {
    List<OmsCartItem> getCartItemByMemberIdSkuId(String memberId, String skuId);

    void putCartItemToDB(OmsCartItem omsCartItem);

    void updataCartItemToDB(OmsCartItem omsCartItem);

    List<OmsCartItem> getDataFromCacheByMemberId(String memberId);




//    void putCartsItemToDB(List<OmsCartItem> omsCartItemList,String memberId);
}
