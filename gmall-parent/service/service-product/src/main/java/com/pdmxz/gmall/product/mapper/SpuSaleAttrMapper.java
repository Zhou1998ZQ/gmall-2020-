package com.pdmxz.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pdmxz.gmall.model.product.SpuSaleAttr;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {
    List<SpuSaleAttr> spuSaleAttrList(@Param("spuId") Long spuId);

    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@Param("skuId") Long skuId,@Param("spuId") Long spuId);
}
