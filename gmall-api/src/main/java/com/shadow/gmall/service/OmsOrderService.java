package com.shadow.gmall.service;

import com.shadow.gmall.beans.OmsOrder;

public interface OmsOrderService {
    void putOmsOderToDB(OmsOrder omsOrder);

    OmsOrder getOrderByOrderSn(String orderSn);

    void updateOrder(OmsOrder omsOrder);

    void sendOrderMsg(String out_trade_no);
}
