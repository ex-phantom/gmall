package com.shadow.gmall.payment.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.shadow.gmall.MQLinked.conf.ActiveMQConfig;
import com.shadow.gmall.MQLinked.util.ActiveMQUtil;
import com.shadow.gmall.beans.OmsOrder;
import com.shadow.gmall.beans.PaymentInfo;
import com.shadow.gmall.myAnnotation.SSOAnnotation;
import com.shadow.gmall.payment.conf.AlipayConfig;
import com.shadow.gmall.service.OmsOrderService;
import com.shadow.gmall.service.PaymentInfoService;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jms.ConnectionFactory;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class paymentHandler {

    @Autowired
    private AlipayClient alipayClient;
    @Reference
    private OmsOrderService omsOrderService;
    @Autowired
    private PaymentInfoService paymentInfoService;



    @SSOAnnotation(isNeedSuccess = true)
    @RequestMapping("alipay/submit")
    @ResponseBody
    public String alipay(String orderSn, BigDecimal totalAmount, ModelMap modelMap) {
        //在支付之前，生成支付信息，方便以后进行对账

        //将omsOrderFromDB的数据，放入payment
        OmsOrder omsOrderFromDB=this.omsOrderService.getOrderByOrderSn(orderSn);
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOrderSn(orderSn);
        paymentInfo.setOrderId(omsOrderFromDB.getId());
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setPaymentStatus("未支付");
        String productName = omsOrderFromDB.getOmsOrderItems().get(0).getProductName();
        System.out.println(productName);
        paymentInfo.setSubject(productName);
        paymentInfo.setTotalAmount(totalAmount);

        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();//创建API对应的request
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);//在公共参数中设置回跳和通知地址
        Map<String,Object> map=new HashMap<>();
        map.put("out_trade_no",orderSn);
        map.put("product_code","FAST_INSTANT_TRADE_PAY");
        map.put("total_amount","0.01");
        map.put("subject","大米");
        String mapJson = JSON.toJSONString(map);
        alipayRequest.setBizContent(mapJson);//填充业务参数
        String form="";
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        System.out.println(form);

        this.paymentInfoService.putPaymentToDB(paymentInfo);

        //在支付宝回调之前，主动访问，查看订单支付情况,使用计数器，控制回访次数
        this.paymentInfoService.sendPayStatus(orderSn,5);
        return form;
    }



    @SSOAnnotation(isNeedSuccess = true)
    @RequestMapping("mx/submit")
    public String mx(String orderSn, BigDecimal totalAmount, ModelMap modelMap, HttpServletRequest request) {
        return null;
    }


    @SSOAnnotation(isNeedSuccess = true)
    @RequestMapping("alipay/callback/return")
    public String callback(ModelMap modelMap, HttpServletRequest request) {
        //在支付回调后验证签名,但是在2.0版本后，同步回调不再将参数放入request域中，所有不能在同步回调中验证签名，只能在异步回调中验证
        //即回调的url必须是公网上的服务器的ip地址
        String sign= request.getParameter("sign");

        String checkPay = this.paymentInfoService.checkPay();



        String trade_no= request.getParameter("trade_no");
        String app_id= request.getParameter("app_id");
        String out_trade_no= request.getParameter("out_trade_no");
        String queryString= request.getQueryString();

        if(!"success".equals(checkPay)){
            //用户支付后，更新支付信息
            PaymentInfo paymentInfo=new PaymentInfo();
            paymentInfo.setPaymentStatus("已支付");
            paymentInfo.setAlipayTradeNo(trade_no);
            paymentInfo.setCallbackContent(queryString);
            paymentInfo.setCallbackTime(new Date());
            paymentInfo.setOrderSn(out_trade_no);

            //使用消息中间件，发送支付成功的消息，异步修改订单“PAY_SUCCESS”
            this.paymentInfoService.sendPayMsg(paymentInfo);

            this.paymentInfoService.updatePaymentInfoToDB(paymentInfo);
        }

        return "redirect:/finish.html";
    }


    @SSOAnnotation(isNeedSuccess = true)
    @RequestMapping("index")
    public String index(String orderSn, String totalAmount, ModelMap modelMap, HttpServletRequest request){
        //从request域中取出数据
        String nickname = (String)request.getAttribute("nickname");
        String memberId = (String)request.getAttribute("memberId");
        modelMap.put("orderSn",orderSn);
        modelMap.put("totalAmount",totalAmount);
        modelMap.put("nickName",nickname);
        return "index";
    }
}
