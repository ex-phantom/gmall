package com.shadow.gmall.service;

import com.shadow.gmall.beans.PaymentInfo;

import java.util.Map;

public interface PaymentInfoService {
    void putPaymentToDB(PaymentInfo paymentInfo);

    void updatePaymentInfoToDB(PaymentInfo paymentInfo);

    void sendPayMsg(PaymentInfo paymentInfo);

    void sendPayStatus(String orderSn,int count);

    Map<String,Object> checkPayStatus(String orderSn);

    String checkPay();
}
