package com.shadow.gmall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.shadow.gmall.beans.PmsSkuAttrValue;
import com.shadow.gmall.beans.PmsSkuImage;
import com.shadow.gmall.beans.PmsSkuInfo;
import com.shadow.gmall.beans.PmsSkuSaleAttrValue;
import com.shadow.gmall.manager.mapper.PmsSkuAttrValueMapper;
import com.shadow.gmall.manager.mapper.PmsSkuImageMapper;
import com.shadow.gmall.manager.mapper.PmsSkuInfoMapper;
import com.shadow.gmall.manager.mapper.PmsSkuSaleAttrValueMapper;
import com.shadow.gmall.redisLinked.redisClient.RedisUtil;
import com.shadow.gmall.service.PmsSkuInfoService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.UUID;

@Service
public class PmsSkuInfoServiceImpl implements PmsSkuInfoService{
    @Autowired
    private PmsSkuInfoMapper pmsSkuInfoMapper;
    @Autowired
    private PmsSkuImageMapper pmsSkuImageMapper;
    @Autowired
    private PmsSkuAttrValueMapper pmsSkuAttrValueMapper;
    @Autowired
    private PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        //设置productid
        pmsSkuInfo.setProductId(pmsSkuInfo.getSpuId());
        //保存skuinfo信息
        this.pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
        String skuid = pmsSkuInfo.getId();
        //保存skuinfo图片信息
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage pmsSkuImage : skuImageList) {
            pmsSkuImage.setSkuId(skuid);
            this.pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }
        //保存skuinfo平台属性值
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
            pmsSkuAttrValue.setSkuId(skuid);
            this.pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);

        }
        //保存skuinfo销售属性值
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
            pmsSkuSaleAttrValue.setSkuId(skuid);
            this.pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }

    }
    @Override
    public PmsSkuInfo queryByIdFromRedisExample(String skuId) {
        //连接redis
        Jedis jedis = redisUtil.getJedis();
        RLock lock = redissonClient.getLock("sku:" + skuId + ":Lock");

        lock.lock();
        try {
            PmsSkuInfo pmsSkuInfo =null;
            //从redis中查询
            String skuInfo = jedis.get("sku:" + skuId + ":Info");
            if(StringUtils.isNotBlank(skuInfo)){
                System.out.println(Thread.currentThread().getName()+"从redis中获取数据输出");
                //有，直接返回
                pmsSkuInfo=JSON.parseObject(skuInfo, PmsSkuInfo.class);
                return pmsSkuInfo;
            }

            pmsSkuInfo = queryById(skuId);
            try {
                System.out.println(Thread.currentThread().getName()+"从mysql中获取数据输出前休眠");
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //同步到redis中
            jedis.set("sku:" + skuId + ":Info",JSON.toJSONString(pmsSkuInfo));
            //有，返回到页面
            return pmsSkuInfo;
        }finally {
            jedis.close();
            lock.unlock();
        }
    }
    @Override
    public PmsSkuInfo queryByIdFromRedis(String skuId) {
        //连接redis
        Jedis jedis = redisUtil.getJedis();
        String valueLockStr = null;
        PmsSkuInfo pmsSkuInfo =null;
        try{
            //从redis中查询
            String skuInfo = jedis.get("sku:" + skuId + ":Info");
            if(StringUtils.isNotBlank(skuInfo)){
                System.out.println(Thread.currentThread().getName()+"从redis中获取数据输出");
                //有，直接返回
                pmsSkuInfo=JSON.parseObject(skuInfo, PmsSkuInfo.class);
                return pmsSkuInfo;
            }
            valueLockStr= UUID.randomUUID().toString();
            String OK = jedis.set("sku:" + skuId + ":lock", valueLockStr, "nx", "px", 1000);
            if(StringUtils.isNotBlank(OK)&&"OK".equals(OK)){
                //没有，从mysql中查询
                pmsSkuInfo = queryById(skuId);

                if(pmsSkuInfo!=null){

//                    try {
//                        System.out.println(Thread.currentThread().getName()+"获取数据输出前休眠");
//                        Thread.sleep(60000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    //同步到redis中
                    jedis.set("sku:" + skuId + ":Info",JSON.toJSONString(pmsSkuInfo));

                    //删除锁
                    String valueLock = jedis.get("sku:" + skuId + ":lock");
                    if(StringUtils.isNotBlank(valueLock)&&valueLock.equals(valueLockStr)){
                        System.out.println(Thread.currentThread().getName()+"删除锁");
                        jedis.del("sku:" + skuId + ":lock");
                    }
                    //有，返回到页面
                    return pmsSkuInfo;
                }
            }else{
//                try {
//                    System.out.println(Thread.currentThread().getName()+"未获取数据自旋前休眠");
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                //当前线程下，结束该方法，开始下一个方法
                return queryByIdFromRedis(skuId);
            }
        }finally {
            //关闭连接
            jedis.close();
        }
        //没有，返回null
        return pmsSkuInfo;
    }

    @Override
    public PmsSkuInfo queryById(String skuId) {
        //查询skuinfo的信息
        PmsSkuInfo pmsSkuInfo=new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo pmsSkuInfo1 = this.pmsSkuInfoMapper.selectOne(pmsSkuInfo);
        //查询skuinfoimage的信息
        PmsSkuImage pmsSkuImage=new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> pmsSkuImageList = this.pmsSkuImageMapper.select(pmsSkuImage);
        pmsSkuInfo1.setSkuImageList(pmsSkuImageList);
        return pmsSkuInfo1;
    }

    @Override
    public List<PmsSkuInfo> queryByProductId(String spuId) {
        PmsSkuInfo pmsSkuInfo=new PmsSkuInfo();
        pmsSkuInfo.setProductId(spuId);
        //查到所有的skuid
        List<PmsSkuInfo> pmsSkuInfoList = this.pmsSkuInfoMapper.select(pmsSkuInfo);
        for (PmsSkuInfo skuInfo : pmsSkuInfoList) {
            String skuid = skuInfo.getId();
            PmsSkuSaleAttrValue pmsSkuSaleAttrValue=new PmsSkuSaleAttrValue();
            pmsSkuSaleAttrValue.setSkuId(skuid);
            //查到skuid对应的具体的销售属性值的id
            List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValueList = this.pmsSkuSaleAttrValueMapper.select(pmsSkuSaleAttrValue);
            skuInfo.setSkuSaleAttrValueList(pmsSkuSaleAttrValueList);
        }
        return pmsSkuInfoList;
    }

    @Override
    public List<PmsSkuInfo> selectAll() {
        //查询所有的pmsSkuInfo，包含有对应的pmsSkuAttrValue
        List<PmsSkuInfo> pmsSkuInfoList = this.pmsSkuInfoMapper.selectAll();
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfoList) {
            PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(pmsSkuInfo.getId());
            List<PmsSkuAttrValue> select = this.pmsSkuAttrValueMapper.select(pmsSkuAttrValue);
            pmsSkuInfo.setSkuAttrValueList(select);
        }
        return pmsSkuInfoList;
    }
}
