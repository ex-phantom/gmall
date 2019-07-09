package com.shadow.gmall.service;

import com.shadow.gmall.beans.OmsCartItem;

import java.util.List;

public interface OmsCartItemCacheService {


    List<OmsCartItem> getDataFromCacheByMemberId(String memberId);
}
