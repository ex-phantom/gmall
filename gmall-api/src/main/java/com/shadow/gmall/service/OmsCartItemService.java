package com.shadow.gmall.service;

import com.shadow.gmall.beans.OmsCartItem;

import java.util.List;

public interface OmsCartItemService {
    List<OmsCartItem> getCartItemByMemberIdSkuId(String memberId, String skuId);

    void putCartItemToDB(OmsCartItem omsCartItem);

    void updataCartItemToDB(OmsCartItem omsCartItem);
}
