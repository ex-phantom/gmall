package com.shadow.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.shadow.gmall.beans.OmsCartItem;
import com.shadow.gmall.redisLinked.redisClient.RedisUtil;
import com.shadow.gmall.service.OmsCartItemCacheService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OmsCartItemCacheServiceImpl  implements OmsCartItemCacheService {
    @Autowired
    private RedisUtil redisUtil;


    @Override
    public List<OmsCartItem> getDataFromCacheByMemberId(String memberId) {
        //获得redis的连接
        Jedis jedis=null;
        List<OmsCartItem> omsCartItemList=new ArrayList<>();
        try{
            jedis= this.redisUtil.getJedis();
            //根据key查询redis中hash结构所有的值
            String key="user:"+memberId+":info";
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
