package com.shadow.gmall.cart.controller;

import Utils.CookieUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.shadow.gmall.beans.OmsCartItem;
import com.shadow.gmall.beans.PmsSkuInfo;
import com.shadow.gmall.service.OmsCartItemCacheService;
import com.shadow.gmall.service.OmsCartItemService;
import com.shadow.gmall.service.PmsSkuInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class CartHandler {

    @Reference
    private OmsCartItemCacheService omsCartItemCacheService;

    @Reference
    private PmsSkuInfoService pmsSkuInfoService;

    @Reference
    private OmsCartItemService omsCartItemService;


    @RequestMapping("checkCart")
    public String checkCart(HttpServletRequest request,OmsCartItem omsCartItem,ModelMap map){
        List<OmsCartItem> omsCartItemList=new ArrayList<>();
        //判断用户是否登陆
        String memberId="";
        if(StringUtils.isBlank(memberId)){
            //根据页面上的选中状态去改变cookies中对应商品的选中状态
            String cookieValue = CookieUtil.getCookieValue(request, "carItemCookies", true);
            if(StringUtils.isNotBlank(cookieValue)){
                omsCartItemList= JSON.parseArray(cookieValue, OmsCartItem.class);
                for (OmsCartItem cartItem : omsCartItemList) {
                    if(cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())){
                        //将页面上商品的选中状态设置为cookies中对应商品的选中状态
                        cartItem.setIsChecked(omsCartItem.getIsChecked());
                    }
                }
            }
        }else{
            omsCartItem.setMemberId(memberId);
            //根据页面上的选中状态去改变db中对应商品的选中状态
            this.omsCartItemService.updataCartItemToDB(omsCartItem);
            //从缓存中取出购物车的数据
            omsCartItemList=this.omsCartItemCacheService.getDataFromCacheByMemberId(memberId);
            //判断能否从缓存中直接取出所有的商品信息
            if(omsCartItemList==null){
                String skuId=null;
                omsCartItemList=this.omsCartItemService.getCartItemByMemberIdSkuId(memberId,skuId);
            }
        }

        //局部修改商品数量后，总价也会发生改变
        map.addAttribute("cartList",omsCartItemList);
        map.addAttribute("cartSumPrice",getcartSumPrice(omsCartItemList));
        return "omsCartItemInner";
    }

    /*购物车列表
     */
    @RequestMapping("cartList")
    public String cartList(HttpServletRequest request, ModelMap map){
        List<OmsCartItem> omsCartItemList=new ArrayList<>();
        //判断用户是否登陆
        String memberId="";
        if(StringUtils.isBlank(memberId)){
            //从cookies中取值操作
            String cookieValue = CookieUtil.getCookieValue(request, "carItemCookies", true);
            if(StringUtils.isNotBlank(cookieValue)){
                omsCartItemList= JSON.parseArray(cookieValue, OmsCartItem.class);
                //cookies中已经设置了总价格
               for (OmsCartItem omsCartItem : omsCartItemList) {
                    omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
               }
            }
        }else{
            //从缓存中取值操作,**********web端怎么能直接操作数据库
//************redisUtil.getUtil();
            //数据库中没有总价格的字段
            omsCartItemList=this.omsCartItemCacheService.getDataFromCacheByMemberId(memberId);
            //判断能否从缓存中直接取出所有的商品信息
            if(omsCartItemList==null){
                String skuId=null;
                omsCartItemList=this.omsCartItemService.getCartItemByMemberIdSkuId(memberId,skuId);
            }

        }
        //计算所有选中商品的总价格
        map.addAttribute("cartList",omsCartItemList);
        map.addAttribute("cartSumPrice",this.getcartSumPrice(omsCartItemList));
        return "cartList";
    }


    //获取商品的总价格
    private Object getcartSumPrice(List<OmsCartItem> omsCartItemList) {
        BigDecimal cartSumPrice = new BigDecimal("0");
        for (OmsCartItem omsCartItem : omsCartItemList) {
            //判断循环中商品的选中状态
            if("1".equals(omsCartItem.getIsChecked())){
                //只有选中的才添加
                BigDecimal totalPrice = omsCartItem.getTotalPrice();
                cartSumPrice = cartSumPrice.add(totalPrice);
            }
        }
        return cartSumPrice;
    }


    @RequestMapping("addToCart")
    public String addToCart(OmsCartItem omsCartItem, HttpServletRequest request, HttpServletResponse response){
        //根据productSkuId也就是skuId查出具体的sku信息
        String skuId = omsCartItem.getProductSkuId();

        PmsSkuInfo pmsSkuInfo = this.pmsSkuInfoService.queryById(skuId);
        //新建一个购物车，供下面的分支使用
        List<OmsCartItem> omsCartItemList=new ArrayList<>();
        //将PmsSkuInfo的信息输入OmsCartItem
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setIsChecked("1");
        omsCartItem.setPrice(pmsSkuInfo.getPrice());
        omsCartItem.setProductCategoryId(pmsSkuInfo.getCatalog3Id());
        omsCartItem.setProductId(pmsSkuInfo.getProductId());
        omsCartItem.setProductName(pmsSkuInfo.getSkuName());
        omsCartItem.setProductPic(pmsSkuInfo.getSkuDefaultImg());
        omsCartItem.setProductSkuId(pmsSkuInfo.getId());
        omsCartItem.setTotalPrice(pmsSkuInfo.getPrice().multiply(omsCartItem.getQuantity()));

        //使用用户id判断用户是否登陆
        String memberId= "";
        if(StringUtils.isBlank(memberId)){
            //未登录，从浏览器中获取cookie,不同步cookies进入缓存
            String cookieValue = CookieUtil.getCookieValue(request, "carItemCookies", true);
            //判断购物车是否存在，购物车不存在，创建购物车，添加商品
            if(StringUtils.isNotBlank(cookieValue)){
                //已有购物车，转为JSON集合对象
                omsCartItemList= JSON.parseArray(cookieValue, OmsCartItem.class);
                //遍历购物车，判断是否有相同的商品,有相同的商品就增加数量，没有就添加商品
                boolean b=this.ifSameProduct(omsCartItemList,skuId);
                if(b){
                    for (OmsCartItem cartItem : omsCartItemList) {
                        if(cartItem.getProductSkuId().equals(skuId)){
                            //修改购物车中商品的数量
                            cartItem.setQuantity(cartItem.getQuantity().add(omsCartItem.getQuantity()));
                            //修改购物车中的商品总价
                            cartItem.setTotalPrice(cartItem.getPrice().multiply(cartItem.getQuantity()));
                            break;
                        }
                    }
                    //将修改后的购物车，转为JSON,放入浏览器
                }else{
                    //购物车中没有相同的商品，将商品添加进入购物车
                    omsCartItemList.add(omsCartItem) ;
                 }
            }else{
                //用户未登录，购物车不存在，添加商品，创建购物车，转为JSON,放入浏览器
                omsCartItemList.add(omsCartItem);
            }
            //公共代码，将购物车对象转为JSON,放入浏览器
            String omsCartJSONValue = JSON.toJSONString(omsCartItemList);
            CookieUtil.setCookie(request,response,"carItemCookies",omsCartJSONValue,1000*60*60*24,true);
        }else{
            //已登录，每次操作数据都进行缓存的同步，从db中的购物车中查询是否存在该商品的信息
            //将选中商品信息放入db
            omsCartItem.setMemberId(memberId);
            omsCartItem.setMemberNickname(null);
            //已知memberId,和商品的skuID查询对应的cartItem对象信息
            List<OmsCartItem> cartItemByMemberIdSkuId = this.omsCartItemService.getCartItemByMemberIdSkuId(memberId, skuId);
            if(cartItemByMemberIdSkuId==null || cartItemByMemberIdSkuId.size()==0){
                //存入数据库，同步缓存
                this.omsCartItemService.putCartItemToDB(omsCartItem);
            }else{
                OmsCartItem omsCartItemExist=cartItemByMemberIdSkuId.get(0);
                //修改db中商品的数量
                omsCartItemExist.setQuantity(omsCartItemExist.getQuantity().add(omsCartItem.getQuantity()));
                //修改db中的商品总价
                omsCartItemExist.setTotalPrice(omsCartItemExist.getPrice().multiply(omsCartItemExist.getQuantity()));
                //存入数据库，同步缓存
                this.omsCartItemService.updataCartItemToDB(omsCartItemExist);
            }
        }
        return "redirect:/success.html";
    }



    //遍历购物车，判断是否有相同的商品,true代表有相同的商品
    private boolean ifSameProduct(List<OmsCartItem> omsCartItemList, String skuId) {
        boolean b=false;
        for (OmsCartItem cartItem : omsCartItemList) {
            if(cartItem.getProductSkuId().equals(skuId)){
                b=true;
                break;
            }
        }
        return b;
    }

}
