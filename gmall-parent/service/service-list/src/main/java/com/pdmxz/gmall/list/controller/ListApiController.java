package com.pdmxz.gmall.list.controller;


import com.pdmxz.gmall.common.result.Result;
import com.pdmxz.gmall.list.service.ListService;
import com.pdmxz.gmall.model.list.Goods;
import com.pdmxz.gmall.model.list.SearchParam;
import com.pdmxz.gmall.model.list.SearchResponseVo;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/list")
public class ListApiController {


    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private ListService listService;
    /**
     * 创建索引库
     * @return
     */
    @GetMapping("/inner/createIndex")
    public Result createIndex(){
        elasticsearchRestTemplate.createIndex(Goods.class);
        elasticsearchRestTemplate.putMapping(Goods.class);
        return Result.ok();
    }

    /**
     * 商品上架
     * @param skuId
     */
    @GetMapping("/inner/onSale/{skuId}")
    public void onSale(@PathVariable Long skuId){
        listService.onSale(skuId);
    }

    /**
     * 商品下架
     * @param skuId
     */
    @GetMapping("/inner/onCancel/{skuId}")
    public void onCancel(@PathVariable Long skuId){
        listService.onCancel(skuId);
    }

    //暴露给访问详情微服务使用
    @PostMapping("/incrHotScore/{skuId}/{score}")
    public Result incrHotScore(@PathVariable Long skuId,@PathVariable Integer score){
        listService.incrHotScore(skuId,score);
        return Result.ok();
    }

    //暴露给web微服务使用
    @PostMapping("/searchResponseVo")
    public SearchResponseVo searchResponseVo(@RequestBody SearchParam searchParam){
        return listService.searchResponseVo(searchParam);
    }


}
