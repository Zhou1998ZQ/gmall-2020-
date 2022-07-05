package com.pdmxz.gmall.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.pdmxz.gmall.common.result.Result;
import com.pdmxz.gmall.model.product.*;
import com.pdmxz.gmall.product.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/product")
public class ManageController {

    @Autowired
    private ManageService manageService;

    @GetMapping("/getCategory1")
    public Result getCategory1() {
        List<BaseCategory1> baseCategory1List = manageService.getCategory1();
        return Result.ok(baseCategory1List);
    }


    @GetMapping("/getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable Long category1Id) {
        List<BaseCategory2> baseCategory2List = manageService.getCategory2(category1Id);
        return Result.ok(baseCategory2List);
    }

    @GetMapping("/getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable Long category2Id) {
        List<BaseCategory3> baseCategory3List = manageService.getCategory3(category2Id);
        return Result.ok(baseCategory3List);
    }

    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(@PathVariable Long category1Id,
                               @PathVariable Long category2Id,
                               @PathVariable Long category3Id) {
        List<BaseAttrInfo> BaseAttrInfoList = manageService.attrInfoList(category1Id, category2Id, category3Id);
        return Result.ok(BaseAttrInfoList);
    }

    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo) {
        manageService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }

    @GetMapping("/getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable Long attrId) {
        List<BaseAttrValue> attrValueList = manageService.getAttrValueList(attrId);
        return Result.ok(attrValueList);
    }

    @GetMapping("/baseTrademark/{page}/{limit}")
    public Result baseTrademark(@PathVariable Integer page, @PathVariable Integer limit) {
        IPage iPage = manageService.baseTrademark(page, limit);
        return Result.ok(iPage);
    }


    @GetMapping("/baseTrademark/get/{id}")
    public Result get(@PathVariable Long id) {
        BaseTrademark baseTrademark = manageService.get(id);
        return Result.ok(baseTrademark);
    }

    @PostMapping("/baseTrademark/save")
    public Result save(@RequestBody BaseTrademark baseTrademark) {
        manageService.save(baseTrademark);
        return Result.ok();
    }

    @DeleteMapping("/baseTrademark/remove/{id}")
    public Result remove(@PathVariable Long id) {
        manageService.remove(id);
        return Result.ok();
    }


    @PutMapping("/baseTrademark/update")
    public Result update(@RequestBody BaseTrademark BaseTrademark) {
        manageService.update(BaseTrademark);
        return Result.ok();
    }

    //http://api.gmall.com/admin/product/1/10?category3Id=61
    @GetMapping("{page}/{limit}")
    public Result page(@PathVariable Integer page, @PathVariable Integer limit, Long category3Id) {
        IPage<SpuInfo> iPage = manageService.page(page, limit, category3Id);
        return Result.ok(iPage);
    }

    @GetMapping("baseSaleAttrList")
    public Result baseSaleAttrList() {
        List<BaseSaleAttr> list = manageService.baseSaleAttrList();
        return Result.ok(list);
    }

    @GetMapping("/baseTrademark/getTrademarkList")
    public Result getTrademarkList() {
        List<BaseTrademark> list = manageService.getTrademarkList();
        return Result.ok(list);
    }

    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        manageService.saveSpuInfo(spuInfo);
        return Result.ok();
    }

    @GetMapping("/spuImageList/{spuId}")
    public Result spuImageList(@PathVariable Long spuId){
       List<SpuImage> list =  manageService.spuImageList(spuId);
       return Result.ok(list);
    }

    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable Long spuId){
        List<SpuSaleAttr> list =  manageService.spuSaleAttrList(spuId);
        return Result.ok(list);
    }

    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
        manageService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    @GetMapping("/list/{page}/{limit}")
    public Result list(@PathVariable Integer page,@PathVariable Integer limit){
        IPage<SkuInfo> iPage =manageService.list(page,limit);
        return Result.ok(iPage);
    }

    @PutMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable Long skuId){
        manageService.onSale(skuId);
        return Result.ok();
    }

    @PutMapping("/cancelSale/{skuId}")
    public Result cancelSale(@PathVariable Long skuId){
        manageService.cancelSale(skuId);
        return Result.ok();
    }
}
