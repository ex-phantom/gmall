package com.shadow.gmall.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.shadow.gmall.beans.PmsProductImage;
import com.shadow.gmall.manager.mapper.PmsProductImageMapper;
import com.shadow.gmall.service.PmsProductImageService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class PmsProductImageServiceImpl implements PmsProductImageService{
    @Autowired
    private PmsProductImageMapper pmsProductImageMapper;
    @Override
    public List<PmsProductImage> getspuImageList(String spuId) {
        PmsProductImage pmsProductImage=new PmsProductImage();
        pmsProductImage.setProductId(spuId);
        List<PmsProductImage> pmsProductImageList=this.pmsProductImageMapper.select(pmsProductImage);
        return pmsProductImageList;
    }
}
