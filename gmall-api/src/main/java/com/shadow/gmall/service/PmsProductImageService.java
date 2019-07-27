package com.shadow.gmall.service;

import com.shadow.gmall.beans.PmsProductImage;

import java.util.List;

public interface PmsProductImageService {
    List<PmsProductImage> getspuImageList(String spuId);
}
