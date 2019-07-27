package com.shadow.gmall.service;

import com.shadow.gmall.beans.PmsBaseAttrValue;

import java.util.List;

public interface PmsBaseAttrValueService {
    List<PmsBaseAttrValue> getAttrValueList(String attrId);
}
