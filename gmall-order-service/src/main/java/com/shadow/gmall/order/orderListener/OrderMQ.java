package com.shadow.gmall.order.orderListener;


import com.shadow.gmall.beans.OmsOrder;
import com.shadow.gmall.service.OmsOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.math.BigDecimal;
import java.util.Date;

@Component
public class OrderMQ {
    @Autowired
    private OmsOrderService omsOrderService;

    @JmsListener(containerFactory = "jmsQueueListener",destination = "PAY_SUCCESS")
    public void consumePayMsg(MapMessage mapMessage){
        //接受传输的消息，修改订单信息，
        OmsOrder omsOrder = new OmsOrder();
        try {
            String out_trade_no = mapMessage.getString("out_trade_no");
            String payAmount = mapMessage.getString("payAmount");
            omsOrder.setStatus("订单已支付");
            omsOrder.setOrderSn(out_trade_no);
            omsOrder.setTotalAmount(new BigDecimal(payAmount));
            omsOrder.setPaymentTime(new Date());

            this.omsOrderService.updateOrder(omsOrder);
            //通知库存发货,"order_success"
            this.omsOrderService.sendOrderMsg(out_trade_no);

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
