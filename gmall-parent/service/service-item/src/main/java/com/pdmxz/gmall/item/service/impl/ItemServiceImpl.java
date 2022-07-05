package com.pdmxz.gmall.item.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.pdmxz.gmall.feign.ProductFeignClient;
import com.pdmxz.gmall.item.service.ItemService;
import com.pdmxz.gmall.list.feign.ListFeignClient;
import com.pdmxz.gmall.model.product.BaseCategoryView;
import com.pdmxz.gmall.model.product.SkuInfo;
import com.pdmxz.gmall.model.product.SpuSaleAttr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Component
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ListFeignClient listFeignClient;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    @Override
    public Map getItem(Long skuId) {
        Map result = new HashMap();

        CompletableFuture<SkuInfo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            result.put("skuInfo", skuInfo);
            return skuInfo;
        }, threadPoolExecutor);

        CompletableFuture<Void> categoryViewCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync((skuInfo) -> {
            BaseCategoryView categoryView = productFeignClient.getCategory(skuInfo.getCategory3Id());
            result.put("categoryView",categoryView);
        },threadPoolExecutor);

        CompletableFuture<Void> priceCompletableFuture = CompletableFuture.runAsync(() -> {
            BigDecimal price = productFeignClient.getPrice(skuId);
            result.put("price", price);
        }, threadPoolExecutor);


        CompletableFuture<Void> spuSaleAttrListCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync((skuInfo) -> {
            List<SpuSaleAttr> spuSaleAttrList = productFeignClient.getSpuSaleAttrListCheckBySku(skuId, skuInfo.getSpuId());
            result.put("spuSaleAttrList",spuSaleAttrList);
        }, threadPoolExecutor);

        CompletableFuture<Void> skuValueIdsMapCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync((skuInfo) -> {
            Map skuValueIdsMap = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
            String valuesSkuJson = JSONObject.toJSONString(skuValueIdsMap);
            result.put("valuesSkuJson", valuesSkuJson);
        }, threadPoolExecutor);

        CompletableFuture.allOf(skuInfoCompletableFuture,categoryViewCompletableFuture
        ,priceCompletableFuture,spuSaleAttrListCompletableFuture,skuValueIdsMapCompletableFuture
        ).join();

        CompletableFuture.runAsync(()->{
            listFeignClient.incrHotScore(skuId,1);
        }, threadPoolExecutor);

        return result;
    }
}
