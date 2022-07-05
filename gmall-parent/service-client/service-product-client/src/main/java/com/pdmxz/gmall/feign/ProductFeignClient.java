package com.pdmxz.gmall.feign;

import com.pdmxz.gmall.feign.fallback.ProductFeignClientFallback;
import com.pdmxz.gmall.model.product.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient(name = "service-product",fallback = ProductFeignClientFallback.class)
public interface ProductFeignClient {
    @GetMapping("/api/product/inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable Long skuId);

    //inner -> 远程调用，不对外暴露 查询三级分类Id和名称
    @GetMapping("/api/product/inner/getCategory/{category3Id}")
    public BaseCategoryView getCategory(@PathVariable Long category3Id);

    //inner -> 远程调用，不对外暴露 单独查询价格，因为价格不能放入缓存中
    @GetMapping("/api/product/inner/getPrice/{skuId}")
    public BigDecimal getPrice(@PathVariable Long skuId);

    //**难** inner -> 远程调用，不对外暴露 通过skuId，spuId获取商品对应的属性和属性值以及被选中的库存
    @GetMapping("/api/product/inner/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable Long skuId, @PathVariable Long spuId);

    //**难** inner -> 远程调用，不对外暴露 通过spuId 获取所有商品对应的销售属性值的集合
    @GetMapping("/api/product/inner/getSkuValueIdsMap/{spuId}")
    public Map getSkuValueIdsMap(@PathVariable Long spuId);

    @GetMapping("/api/product/inner/getIndex")
    List<BaseCategoryView> getIndex();

    @GetMapping("/api/product/inner/getBaseTrademark/{tmId}")
    public BaseTrademark getBaseTrademark(@PathVariable Long tmId);

    @GetMapping("/api/product/inner/getAttrList/{skuId}")
    List<SkuAttrValue> getAttrList(@PathVariable Long skuId);


}
