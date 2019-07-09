package com.shadow.gmall.manage.controller;

import com.shadow.gmall.redisLinked.config.RedisConfig;
import com.shadow.gmall.redisLinked.redisClient.RedisUtil;
import jodd.util.StringUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

@RestController
public class RedissonHandler {
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping("testItem")
    public String lockTest(){
        Jedis jedis = redisUtil.getJedis();// redis链接
        RLock lock = redissonClient.getLock("redis-lock");//分布锁
        //加锁
        lock.lock();
        String v=null;
        try {
            v = jedis.get("k");//获取value
            System.err.println("==>"+v);//打印value
            if(StringUtil.isBlank(v)){
                v = "1";
            }
            long inum = Long.parseLong(v);//获得value的值
            jedis.set("k", inum+1+"");//value增加1
            jedis.close();
        } finally {
            lock.unlock();
        }
        return v;
    }

}
