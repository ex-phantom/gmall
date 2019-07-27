package com.shadow.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shadow.gmall.MQLinked.util.ActiveMQUtil;
import com.shadow.gmall.beans.OmsOrder;
import com.shadow.gmall.beans.OmsOrderItem;
import com.shadow.gmall.order.mapper.OmsOrderItemMapper;
import com.shadow.gmall.order.mapper.OmsOrderMapper;
import com.shadow.gmall.service.OmsOrderService;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.List;

@Service
public class OmsOrderServiceImpl implements OmsOrderService {
    @Autowired
    private OmsOrderMapper omsOrderMapper;

    @Autowired
    private OmsOrderItemMapper omsOrderItemMapper;

    @Autowired
    private ActiveMQUtil activeMQUtil;


    @Override
    public void putOmsOderToDB(OmsOrder omsOrder) {
        this.omsOrderMapper.insertSelective(omsOrder);
        String id = omsOrder.getId();
        List<OmsOrderItem> omsOrderItems = omsOrder.getOmsOrderItems();
        for (OmsOrderItem omsOrderItem : omsOrderItems) {
            omsOrderItem.setOrderId(id);
            this.omsOrderItemMapper.insertSelective(omsOrderItem);
        }
    }

    @Override
    public OmsOrder getOrderByOrderSn(String orderSn) {
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setOrderSn(orderSn);
        OmsOrder omsOrderFromDB = this.omsOrderMapper.selectOne(omsOrder);
        String orderId = omsOrderFromDB.getId();
        OmsOrderItem omsOrderItem=new OmsOrderItem();
        omsOrderItem.setOrderId(orderId);
        List<OmsOrderItem> select = this.omsOrderItemMapper.select(omsOrderItem);
        omsOrderFromDB.setOmsOrderItems(select);
        return omsOrderFromDB;
    }

    @Override
    public void updateOrder(OmsOrder omsOrder) {
        Example e=new Example(OmsOrder.class);
        e.createCriteria().andEqualTo("orderSn",omsOrder.getOrderSn());
        this.omsOrderMapper.updateByExampleSelective(omsOrder,e);
    }

    @Override
    public void sendOrderMsg(String out_trade_no) {
        //发送order修改成功的消息给库存系统
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
            Queue order_success = session.createQueue("ORDER_SUCCESS");
            //创建发送者
            MessageProducer producer = session.createProducer(order_success);
            //创建发送的文本
            MapMessage activeMQMapMessage = new ActiveMQMapMessage();
            activeMQMapMessage.setString("orderSn",out_trade_no);
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
}
