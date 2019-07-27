package com.shadow.gmall.payment.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.shadow.gmall.MQLinked.util.ActiveMQUtil;
import com.shadow.gmall.beans.PaymentInfo;
import com.shadow.gmall.payment.mapper.PaymentInfoMapper;
import com.shadow.gmall.service.PaymentInfoService;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentInfoServiceImpl implements PaymentInfoService {
    @Autowired
    private PaymentInfoMapper paymentInfoMapper;
    @Autowired
    private ActiveMQUtil activeMQUtil;
    @Autowired
    private AlipayClient alipayClient;

    @Override
    public void putPaymentToDB(PaymentInfo paymentInfo) {
        this.paymentInfoMapper.insertSelective(paymentInfo);
    }

    @Override
    public void updatePaymentInfoToDB(PaymentInfo paymentInfo) {
        Example e=new Example(PaymentInfo.class);
        e.createCriteria().andEqualTo("orderSn",paymentInfo.getOrderSn());
        this.paymentInfoMapper.updateByExampleSelective(paymentInfo,e);
    }

    @Override
    public void sendPayMsg(PaymentInfo paymentInfo)  {
        //发送支付成功的消息给订单系统
        //获取mq的连接工厂
        ConnectionFactory connectionFactory = this.activeMQUtil.getConnectionFactory();
        Connection connection =null;
        Session session =null;
        try {
            //创建mq的连接
            connection = connectionFactory.createConnection();
            //创建会话
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            //创建队列
            Queue pay_success = session.createQueue("PAY_SUCCESS");
            //创建发送者
            MessageProducer producer = session.createProducer(pay_success);
            //创建发送的文本
            MapMessage activeMQMapMessage = new ActiveMQMapMessage();
            activeMQMapMessage.setString("out_trade_no",paymentInfo.getOrderSn());
            activeMQMapMessage.setString("status","success");
            activeMQMapMessage.setString("payAmount",paymentInfo.getTotalAmount()+"");
            //发送信息
            producer.send(activeMQMapMessage);
            //提交事务
            session.commit();
        } catch (JMSException e) {
            try {
                session.rollback();
            } catch (JMSException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            try {
                session.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sendPayStatus( String orderSn,int count) {
        //发送支付成功的消息给订单系统
        //获取mq的连接工厂
        ConnectionFactory connectionFactory = this.activeMQUtil.getConnectionFactory();
        Connection connection =null;
        Session session =null;
        try {
            //创建mq的连接
            connection = connectionFactory.createConnection();
            //创建会话
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            //创建队列
            Queue pay_success = session.createQueue("CHECK_PAY_STATUS");
            //创建发送者
            MessageProducer producer = session.createProducer(pay_success);
            //创建发送的文本
            MapMessage activeMQMapMessage = new ActiveMQMapMessage();
            activeMQMapMessage.setString("out_trade_no",orderSn);
            //使用ScheduledMessage形成延时队列，即延时多少秒后，发送消息
            activeMQMapMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY,60*1000);
            activeMQMapMessage.setInt("count",count);
            //发送信息
            producer.send(activeMQMapMessage);
            //提交事务
            session.commit();
        } catch (JMSException e) {
            try {
                session.rollback();
            } catch (JMSException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            try {
                session.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Map<String,Object> checkPayStatus(String orderSn) {
        Map<String,Object> returnMap=new HashMap<>();
        //调用支付宝的接口，确认支付信息
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        Map<String,Object> map=new HashMap<>();
        //将要查询的订单号放入发送的信息集合
        map.put("out_trade_no",orderSn);
        String mapJson = JSON.toJSONString(map);
        request.setBizContent(mapJson);
        AlipayTradeQueryResponse response = null;
        try {
            //执行访问请求
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        //判断响应结果
        if(response.isSuccess()){
            String tradeStatus = response.getTradeStatus();
            returnMap.put("trade_status",tradeStatus);
            returnMap.put("out_trade_no",orderSn);
            System.out.println("调用成功");
        } else {
            returnMap.put("trade_status","");
            returnMap.put("out_trade_no",orderSn);
            System.out.println("调用失败");
        }


        return returnMap;
    }

    @Override
    public String checkPay() {
        return "success";
    }
}
