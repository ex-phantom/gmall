package com.shadow.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.shadow.gmall.Utils.CookieUtil;
import com.shadow.gmall.beans.*;
import com.shadow.gmall.myAnnotation.SSOAnnotation;
import com.shadow.gmall.service.*;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
public class OrderHandler {

    @Reference
    private UmsMemberReceiveAddressService umsMemberReceiveAddressService;

    @Reference
    private OmsOrderItemService omsOrderItemService;

    @Reference
    private OmsOrderService omsOrderService;

    @Reference
    private PmsSkuInfoService pmsSkuInfoService;


    @SSOAnnotation(isNeedSuccess = true)
    @RequestMapping("submitOrder")
    public String submitOrder(String userAddressId,String tradeCode,HttpServletRequest request, ModelMap map){
        //从request域中取出数据
        String nickname = (String)request.getAttribute("nickname");
        String memberId = (String)request.getAttribute("memberId");
        //取出缓存中的tradeCode，与页面携带的进行比较，删除
        boolean b=this.omsOrderItemService.getTradeCodeFromCache(memberId,tradeCode);
        if(!b){
            map.put("errMsg","亲，当前订单已提交");
            return "tradeFail";
        }
        //根据页面传输的用户收货地址的id查出具体信息
        UmsMemberReceiveAddress umsMemberReceiveAddress=this.umsMemberReceiveAddressService.getUserAddressById(userAddressId);

        //查出Cookies中的购物车缓存
        String carItemCookies = CookieUtil.getCookieValue(request, "carItemCookies", true);
        List<OmsCartItem> omsCartItemList = JSON.parseArray(carItemCookies, OmsCartItem.class);
        //根据memberId查出用户的购物车缓存
//      List<OmsCartItem> omsCartItemList = this.omsCartItemService.getDataFromCacheByMemberId(memberId);
        //将订单生成，并插入数据库
        OmsOrder omsOrder=new OmsOrder();
        List<OmsOrderItem> omsOrderItemList=new ArrayList<>();
        //定义orderSn
        SimpleDateFormat time = new SimpleDateFormat("yyyyMMddHHmmss");
        time.format(new Date());
        String orderSn="gmall"+time+System.currentTimeMillis();


        omsOrder.setOrderSn(orderSn);
        omsOrder.setCreateTime(new Date());
        omsOrder.setMemberId(memberId);
        omsOrder.setMemberUsername(nickname);
        omsOrder.setOrderType(0);
        omsOrder.setReceiverCity(umsMemberReceiveAddress.getCity());
        omsOrder.setReceiverDetailAddress(umsMemberReceiveAddress.getDetailAddress());
        omsOrder.setReceiverName(umsMemberReceiveAddress.getName());
        omsOrder.setReceiverPhone(umsMemberReceiveAddress.getPhoneNumber());
        omsOrder.setReceiverPostCode(umsMemberReceiveAddress.getPostCode());
        omsOrder.setReceiverProvince(umsMemberReceiveAddress.getProvince());
        omsOrder.setReceiverRegion(umsMemberReceiveAddress.getRegion());
        omsOrder.setStatus("0");
        BigDecimal totalPrice = this.getTotalPrice(omsCartItemList);
        omsOrder.setTotalAmount(totalPrice);
        omsOrder.setSourceType(0);


        //比较购物车中商品的价格和db中商品的价格
        for (OmsCartItem omsCartItem : omsCartItemList) {
            OmsOrderItem omsOrderItem = new OmsOrderItem();
            //根据购物车中商品的id查出db中具体的商品价格
            String skuId = omsCartItem.getProductSkuId();
  //          List<OmsCartItem> cartItemByMemberIdSkuId = this.omsCartItemService.getCartItemByMemberIdSkuId(memberId, skuId);
            PmsSkuInfo pmsSkuInfo = this.pmsSkuInfoService.queryById(skuId);
  //          OmsCartItem omsCartItemFromDB = cartItemByMemberIdSkuId.get(0);
            //检验价格
            int i=omsCartItem.getPrice().compareTo(pmsSkuInfo.getPrice());
            if(i!=0){
                map.put("errMsg","您购买的商品价格发生了变动，请重新选择");
                return "tradeFail";
            }
            //检验库存。。。。。。。。。。。。。。。。。。。。。。。。。。。

            omsOrderItem.setProductCategoryId(pmsSkuInfo.getCatalog3Id());
            omsOrderItem.setProductSkuId(pmsSkuInfo.getId());
            omsOrderItem.setProductId(pmsSkuInfo.getProductId());
            omsOrderItem.setProductPic(pmsSkuInfo.getSkuDefaultImg());
            omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
            omsOrderItem.setProductName(pmsSkuInfo.getSkuName());
            omsOrderItem.setRealAmount(pmsSkuInfo.getPrice().multiply(omsCartItem.getQuantity()));

            omsOrderItem.setOrderSn(orderSn);
            //先插入order,利用order自主生成的主键，再插入orderItem
//            this.omsOrderItemService.putOrderItemToDB(omsOrderItem);
            omsOrderItemList.add(omsOrderItem);
        }
        omsOrder.setOmsOrderItems(omsOrderItemList);

        this.omsOrderService.putOmsOderToDB(omsOrder);
        return "redirect:http://localhost:8088/index?totalAmount="+totalPrice+"&orderSn="+orderSn;
    }


    @SSOAnnotation(isNeedSuccess = true)
    @RequestMapping("toTrade")
    public String toTrade(HttpServletRequest request, ModelMap map, HttpServletResponse response){

        List<OmsOrderItem> omsOrderItemList=new ArrayList<>();

        //从request域中取出数据
        String nickname = (String)request.getAttribute("nickname");
        String memberId = (String)request.getAttribute("memberId");

        //将cookies中购物车中的商品放入db和缓存中
//        String carItemCookies = CookieUtil.getCookieValue(request, "carItemCookies", true);
//        List<OmsCartItem> omsCartItemList = JSON.parseArray(carItemCookies, OmsCartItem.class);
//        this.omsCartItemService.putCartsItemToDB(omsCartItemList,memberId);

        //在拦截器中已做出了判断
        String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);

        //根据memberId查出用户的收货地址列表
        List<UmsMemberReceiveAddress> umsMemberReceiveAddressList=this.umsMemberReceiveAddressService.getUserAddressByMemberId(memberId);
        //根据memberId查出用户的购物车缓存
//        List<OmsCartItem> omsCartItemList = this.omsCartItemService.getDataFromCacheByMemberId(memberId);
        //查出Cookies中的购物车缓存
        String carItemCookies = CookieUtil.getCookieValue(request, "carItemCookies", true);
        List<OmsCartItem> omsCartItemList = JSON.parseArray(carItemCookies, OmsCartItem.class);
        for (OmsCartItem omsCartItem : omsCartItemList) {
            //被选中的商品才需要进入商品结算
            if("1".equals(omsCartItem.getIsChecked())){
                //将每件商品的详情，放入订单商品详情中
                OmsOrderItem omsOrderItem=new OmsOrderItem();
                omsOrderItem.setProductCategoryId(omsCartItem.getProductCategoryId());
                omsOrderItem.setProductId(omsCartItem.getProductId());
                omsOrderItem.setProductName(omsCartItem.getProductName());
                omsOrderItem.setProductPic(omsCartItem.getProductPic());
                omsOrderItem.setProductPrice(omsCartItem.getPrice());
                omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
                omsOrderItem.setProductSkuId(omsCartItem.getProductSkuId());
                omsOrderItem.setRealAmount(omsOrderItem.getProductPrice().multiply(omsOrderItem.getProductQuantity()));


                //omsOrderItem.
                omsOrderItemList.add(omsOrderItem);
            }

        }
//        for (OmsCartItem omsCartItem : dataFromCacheByMemberId) {
//
//        }
        //解决用户重复点击浏览器后退再次提交订单的问题
        //生成交易码，放入缓存和浏览器，用户进行订单结算时，取出缓存中的交易码，判断是否相等，删除交易码，提交订单
        String tradeCode = UUID.randomUUID().toString();
        //CookieUtil.setCookie(request,response,"user:"+memberId+":tradeCode",tradeCode,60*60*24,true);
        this.omsOrderItemService.putTradeCodeToCache(memberId,tradeCode);

        map.addAttribute("tradeCode",tradeCode);
        map.addAttribute("nickName",nickname) ;
        map.addAttribute("userAddressList",umsMemberReceiveAddressList) ;
        map.addAttribute("orderDetailList",omsOrderItemList) ;
        map.addAttribute("totalAmount",this.getTotalPrice(omsCartItemList)) ;
        //addAttribute不允许添加null,put允许添加null
        return "trade";
    }

    private BigDecimal getTotalPrice(List<OmsCartItem> omsCartItemList) {
        BigDecimal totalAmount=new BigDecimal("0");
        for (OmsCartItem omsCartItem : omsCartItemList) {
            //被选中的商品才需要进入商品结算
            if("1".equals(omsCartItem.getIsChecked())) {
                //将每件商品的详情，放入订单商品详情中
                //将每件商品的价格加入总价
                totalAmount = totalAmount.add(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
            }
        }
        return totalAmount;
    }


}
