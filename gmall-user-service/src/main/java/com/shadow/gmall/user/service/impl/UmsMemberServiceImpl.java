package com.shadow.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.shadow.gmall.beans.OmsCartItem;
import com.shadow.gmall.beans.UmsMember;
import com.shadow.gmall.redisLinked.redisClient.RedisUtil;
import com.shadow.gmall.user.mapper.UmsMemberMapper;
import com.shadow.gmall.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;


import java.util.List;

@Service
public class UmsMemberServiceImpl implements UmsMemberService {
    @Autowired
    private UmsMemberMapper umsMemberMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<UmsMember> getAll() {
        return this.umsMemberMapper.getAll();
    }

    @Override
    public UmsMember getUmsById(String id) {
        UmsMember umsMember=new UmsMember();
        umsMember.setId(id);
        UmsMember umsMember1 = this.umsMemberMapper.selectOne(umsMember);
        return umsMember1;
    }

    @Override
    public Integer delUmsById(String id) {
       int i= this.umsMemberMapper.deleteByPrimaryKey(id);
        return i;
    }

    @Override
    public Integer updataUms(UmsMember umsMember) {
        Integer i= this.umsMemberMapper.updateByPrimaryKeySelective(umsMember);
        return i;
    }


    @Override
    public UmsMember getUmsMemberFromdb(UmsMember umsMember) {
        UmsMember umsMember1=new UmsMember();
        umsMember1.setPassword(umsMember.getPassword());
        umsMember1.setUsername(umsMember.getUsername());
        UmsMember umsMemberFromDB = this.umsMemberMapper.selectOne(umsMember1);

        //同步缓存
        if(umsMemberFromDB!=null){
            Jedis jedis = this.redisUtil.getJedis();
            jedis.setex("user:"+umsMemberFromDB.getId()+":info",60*60, JSON.toJSONString(umsMemberFromDB));
            jedis.close();
        }

        return umsMemberFromDB;

    }

    @Override
    public void putUserToCache(String token, String id) {
        Jedis jedis = this.redisUtil.getJedis();
        jedis.setex("user:"+id+":token",60*60,token);
        jedis.close();
    }
    //将联合账号，插入数据库，如果账号已存在，直接返回，同步缓存
    @Override
    public UmsMember putUmsMemberTodb(UmsMember umsMember) {
        UmsMember umsMemberToDB=new UmsMember();
        umsMemberToDB.setSourceUid(umsMember.getSourceUid());
        UmsMember umsMemberFromDB = this.umsMemberMapper.selectOne(umsMemberToDB);
       //UmsMember umsMemberFromDB = this.umsMemberMapper.selectOneFromDB(umsMember.getSourceUid());
        if(umsMemberFromDB!=null){
            //同步缓存
            Jedis jedis = this.redisUtil.getJedis();
            jedis.setex("user:"+umsMemberFromDB.getId()+":info",60*60, JSON.toJSONString(umsMemberFromDB));
            jedis.close();
            return umsMemberFromDB;
        }else {
            this.umsMemberMapper.insertSelective(umsMember);
            umsMemberToDB=umsMember;
            return umsMemberToDB;
        }
    }


}
