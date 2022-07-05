package com.pdmxz.gmall.product.controller;

import com.pdmxz.gmall.model.product.*;
import com.pdmxz.gmall.product.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
public class ProductApiController {

    @Autowired
    private ManageService manageService;
    //inner -> 远程调用，不对外暴露 查询库存和图片
    @GetMapping("/inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable Long skuId){
       return manageService.getSkuInfo(skuId);
    }

    //inner -> 远程调用，不对外暴露 查询三级分类Id和名称
    @GetMapping("/inner/getCategory/{category3Id}")
    public BaseCategoryView getCategory(@PathVariable Long category3Id){
        return manageService.getCategory(category3Id);
    }

    //inner -> 远程调用，不对外暴露 单独查询价格，因为价格不能放入缓存中
    @GetMapping("/inner/getPrice/{skuId}")
    public BigDecimal getPrice(@PathVariable Long skuId){
        return manageService.getPrice(skuId);
    }

    //**难** inner -> 远程调用，不对外暴露 通过skuId，spuId获取商品对应的属性和属性值以及被选中的库存
    @GetMapping("/inner/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable Long skuId, @PathVariable Long spuId){
       return manageService.getSpuSaleAttrListCheckBySku(skuId,spuId);
    }
    //**难** inner -> 远程调用，不对外暴露 通过spuId 获取所有商品对应的销售属性值的集合
    @GetMapping("/inner/getSkuValueIdsMap/{spuId}")
    public Map getSkuValueIdsMap(@PathVariable Long spuId){
        return manageService.getSkuValueIdsMap(spuId);
    }

    @GetMapping("/inner/getIndex")
    public List<BaseCategoryView> getIndex(){
       return manageService.getIndex();
    }

    @GetMapping("/inner/getBaseTrademark/{tmId}")
    public BaseTrademark getBaseTrademark(@PathVariable Long tmId){
        return manageService.getBaseTrademark(tmId);
    }


    @GetMapping("/inner/getAttrList/{skuId}")
    List<SkuAttrValue> getAttrList(@PathVariable Long skuId){
      return   manageService.getAttrList(skuId);
    }

}
