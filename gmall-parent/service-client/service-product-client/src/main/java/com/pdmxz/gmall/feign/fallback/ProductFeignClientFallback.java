package com.pdmxz.gmall.feign.fallback;

import com.pdmxz.gmall.feign.ProductFeignClient;
import com.pdmxz.gmall.model.product.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class ProductFeignClientFallback implements ProductFeignClient {
    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        return null;
    }

    @Override
    public BaseCategoryView getCategory(Long category3Id) {
        return null;
    }

    @Override
    public BigDecimal getPrice(Long skuId) {
        return null;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {
        return null;
    }

    @Override
    public Map getSkuValueIdsMap(Long spuId) {
        return null;
    }

    @Override
    public List<BaseCategoryView> getIndex() {
        return null;
    }

    @Override
    public BaseTrademark getBaseTrademark(Long tmId) {
        return null;
    }

    @Override
    public List<SkuAttrValue> getAttrList(Long skuId) {
        return null;
    }
}
