package com.shadow.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shadow.gmall.beans.OmsOrderItem;
import com.shadow.gmall.order.mapper.OmsOrderItemMapper;
import com.shadow.gmall.redisLinked.redisClient.RedisUtil;
import com.shadow.gmall.service.OmsOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.Collections;
@Service
public class OmsOrderItemServiceImpl implements OmsOrderItemService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private OmsOrderItemMapper omsOrderItemMapper;

    @Override
    public void putTradeCodeToCache(String memberId,String tradeCode) {
        //获得redis的连接
        Jedis jedis=null;
        try{
            jedis= this.redisUtil.getJedis();
            //根据key查询redis中hash结构所有的值cart
            String key="user:"+memberId+":tradeCode";
            jedis.set(key,tradeCode);
        }finally {
            jedis.close();
        }
    }

    @Override
    public boolean getTradeCodeFromCache(String memberId,String tradeCode) {
        //获得redis的连接
        Jedis jedis=null;
        boolean b=false;
        try{
            jedis= this.redisUtil.getJedis();
            //根据key查询redis中的值
            String key="user:"+memberId+":tradeCode";
            //使用lua脚本操作redis,发现及删除
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

            Long eval = (Long) jedis.eval(script, Collections.singletonList(key),
                    Collections.singletonList(tradeCode));

            if(eval==0){
                b=true;
            }
            return b;
        }finally {
            jedis.close();
        }
    }

    @Override
    public void putOrderItemToDB(OmsOrderItem omsOrderItem) {
        this.omsOrderItemMapper.insertSelective(omsOrderItem);
    }
}
