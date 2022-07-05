package com.pdmxz.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pdmxz.gmall.model.product.SkuSaleAttrValue;

import java.util.List;
import java.util.Map;

public interface SkuSaleAttrValueMapper  extends BaseMapper<SkuSaleAttrValue> {
    List<Map> getSkuValueIdsMap(Long spuId);
}
