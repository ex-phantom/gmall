package com.shadow.gmall.service;

import com.shadow.gmall.beans.OmsCartItem;
import com.shadow.gmall.beans.OmsOrderItem;

public interface OmsOrderItemService {
    void putTradeCodeToCache(String memberId,String tradeCode);

    boolean getTradeCodeFromCache(String memberId,String tradeCode);

    void putOrderItemToDB(OmsOrderItem omsOrderItem);
}
