package com.pdmxz.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pdmxz.gmall.model.product.SkuAttrValue;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SkuAttrValueMapper extends BaseMapper<SkuAttrValue> {
    List<SkuAttrValue> getAttrList(@Param("skuId") Long skuId);
}
