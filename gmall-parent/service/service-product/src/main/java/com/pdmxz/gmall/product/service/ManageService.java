package com.pdmxz.gmall.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pdmxz.gmall.model.product.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ManageService {
    List<BaseCategory1> getCategory1();

    List<BaseCategory2> getCategory2(Long category1Id);

    List<BaseCategory3> getCategory3(Long category2Id);

    List<BaseAttrInfo> attrInfoList(Long category1Id, Long category2Id, Long category3Id);

    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    List<BaseAttrValue> getAttrValueList(Long attrId);

    IPage baseTrademark(Integer page, Integer limit);

    void save(BaseTrademark baseTrademark);

    void remove(Long id);

    BaseTrademark get(Long id);

    void update(BaseTrademark baseTrademark);

    IPage page(Integer page, Integer limit, Long category3Id);

    List<BaseSaleAttr> baseSaleAttrList();

    List<BaseTrademark> getTrademarkList();

    void saveSpuInfo(SpuInfo spuInfo);

    List<SpuImage> spuImageList(Long spuId);

    List<SpuSaleAttr> spuSaleAttrList(Long spuId);

    void saveSkuInfo(SkuInfo skuInfo);

    IPage<SkuInfo> list(Integer page, Integer limit);

    void onSale(Long skuId);

    void cancelSale(Long skuId);

    SkuInfo getSkuInfo(Long skuId);

    BaseCategoryView getCategory(Long category3Id);

    BigDecimal getPrice(Long skuId);

    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId);

    Map getSkuValueIdsMap(Long spuId);

    List<BaseCategoryView> getIndex();

    BaseTrademark getBaseTrademark(Long tmId);

    List<SkuAttrValue> getAttrList(Long skuId);
}
