package com.shadow.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.shadow.gmall.beans.OmsCartItem;
import com.shadow.gmall.cart.mapper.OmsCartItemMapper;
import com.shadow.gmall.redisLinked.redisClient.RedisUtil;
import com.shadow.gmall.service.OmsCartItemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OmsCartItemServiceImpl implements OmsCartItemService{

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private OmsCartItemMapper omsCartItemMapper;


    @Override
    public List<OmsCartItem> getCartItemByMemberIdSkuId(String memberId, String skuId) {
        OmsCartItem omsCartItem=new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        omsCartItem.setProductSkuId(skuId);
        List<OmsCartItem> omsCartItem1 = this.omsCartItemMapper.select(omsCartItem);
        return omsCartItem1;
    }

    @Override
    public void putCartItemToDB(OmsCartItem omsCartItem) {
        this.omsCartItemMapper.insertSelective(omsCartItem);
        //同步缓存
        this.currentCache(omsCartItem.getMemberId());
    }

    @Override
    public void updataCartItemToDB(OmsCartItem omsCartItem) {
        OmsCartItem omsCartItemToDB=new OmsCartItem();
        Example example=new Example(OmsCartItem.class);
        Example.Criteria criteria = example.createCriteria();
        //判断是那种操作来修改数据库中的商品信息
        if(StringUtils.isBlank(omsCartItem.getId())){
            //购物车列表页面商品的选中状态
            omsCartItemToDB.setIsChecked(omsCartItem.getIsChecked());
            criteria.andEqualTo("productSkuId",omsCartItem.getProductSkuId());
            criteria.andEqualTo("memberId",omsCartItem.getMemberId());
        }else{
            //商品页面添加相同商品进入购物车
            omsCartItemToDB.setQuantity(omsCartItem.getQuantity());
            omsCartItemToDB.setTotalPrice(omsCartItem.getTotalPrice());

            criteria.andEqualTo("id",omsCartItem.getId());
        }
        this.omsCartItemMapper.updateByExampleSelective(omsCartItemToDB,example);
        //同步缓存
        this.currentCache(omsCartItem.getMemberId());
//        this.omsCartItemMapper.updateByPrimaryKeySelective(omsCartItem);
    }

//    @Override
//    public void putCartsItemToDB(List<OmsCartItem> omsCartItemList,String memberId) {
//
//        for (OmsCartItem omsCartItem : omsCartItemList) {
//            this.omsCartItemMapper.insertSelective(omsCartItem);
//        }
//        //同步缓存
//        this.currentCache(memberId);
//    }


    //同步缓存商品,通过memberid查询该用户下所有的商品，注入redis缓存
    private void currentCache(String memberId) {
        //获得redis的连接
        Jedis jedis=null;
        Map<String,String> map=new HashMap<>();
        try{
            jedis= this.redisUtil.getJedis();
            //通过memberid查询该用户下所有的商品
            OmsCartItem omsCartItemExample=new OmsCartItem();
            omsCartItemExample.setMemberId(memberId);
            List<OmsCartItem> omsCartItemList = this.omsCartItemMapper.select(omsCartItemExample);
            //注入redis缓存
            if(omsCartItemList!=null){
                String key="user:"+memberId+":cart";
                for (OmsCartItem omsCartItem : omsCartItemList) {
                    String SkuId = omsCartItem.getProductSkuId();
                    String omsCartItemJSON = JSON.toJSONString(omsCartItem);
                    map.put(SkuId,omsCartItemJSON);
                }
                jedis.hmset(key,map);
            }
        }finally {
            jedis.close();
        }
    }


    @Override
    public List<OmsCartItem> getDataFromCacheByMemberId(String memberId) {
        //获得redis的连接
        Jedis jedis=null;
        List<OmsCartItem> omsCartItemList=new ArrayList<>();
        try{
            jedis= this.redisUtil.getJedis();
            //根据key查询redis中hash结构所有的值cart
            String key="user:"+memberId+":cart";
            List<String> omsCartItemStrs = jedis.hvals(key);
            if(omsCartItemStrs!=null){
                for (String omsCartItemStr : omsCartItemStrs) {
                    //解析
                    OmsCartItem omsCartItem = JSON.parseObject(omsCartItemStr, OmsCartItem.class);
                    //redis数据库中没有总价格的字段,需要设置
                    omsCartItem.setTotalPrice(omsCartItem.getQuantity().multiply(omsCartItem.getPrice()));
                    omsCartItemList.add(omsCartItem);
                }
            }
        }finally {
            jedis.close();
        }
        return omsCartItemList;
    }
}
