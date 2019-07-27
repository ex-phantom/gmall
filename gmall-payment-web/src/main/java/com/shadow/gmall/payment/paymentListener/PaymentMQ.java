package com.shadow.gmall.payment.paymentListener;

import com.shadow.gmall.beans.OmsOrder;
import com.shadow.gmall.beans.PaymentInfo;
import com.shadow.gmall.service.OmsOrderService;
import com.shadow.gmall.service.PaymentInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Component
public class PaymentMQ {


    @Autowired
    private PaymentInfoService paymentInfoService ;

    @JmsListener(containerFactory = "jmsQueueListener",destination = "CHECH_PAY_STATUS")
    public void consumePayStatus(MapMessage mapMessage) throws JMSException {

        String orderSn = mapMessage.getString("out_trade_no");
        int count = mapMessage.getInt("count");
        //检查订单的状态
        System.out.println("检查订单："+orderSn+"的状态");
        Map<String, Object> stringObjectMap = this.paymentInfoService.checkPayStatus(orderSn);
        String trade_status = (String)stringObjectMap.get("trade_status");
        count--;
        //判断订单支付状态
        if(StringUtils.isNotBlank(trade_status)){
            //只有在订单已创建，未支付的情况下，才继续发送消息确认支付
            if("WAIT_BUYER_PAY".equals(trade_status)){
                if(count>0){
                    //发现订单未支付，继续进行发送消息确认
                    this.paymentInfoService.sendPayStatus(orderSn,count);
                }
            }
            //将支付信息更新入数据库，发送延时队列
            PaymentInfo paymentInfo=new PaymentInfo();
            paymentInfo.setOrderSn(orderSn);
            paymentInfo.setPaymentStatus(trade_status);
            //进行幂等性检查
            String status=this.paymentInfoService.checkPay();
            if(!"success".equals(status)){
                this.paymentInfoService.updatePaymentInfoToDB(paymentInfo);
                this.paymentInfoService.sendPayMsg(paymentInfo);
            }

        }else {
            if(count>0){
                //发现订单未支付，继续进行发送消息确认
                this.paymentInfoService.sendPayStatus(orderSn,count);
            }
        }
    }
}
