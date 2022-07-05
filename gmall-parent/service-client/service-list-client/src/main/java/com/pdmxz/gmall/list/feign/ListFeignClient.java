package com.pdmxz.gmall.list.feign;


import com.pdmxz.gmall.common.result.Result;
import com.pdmxz.gmall.list.feign.fallback.ListFeignClientFallback;
import com.pdmxz.gmall.model.list.SearchParam;
import com.pdmxz.gmall.model.list.SearchResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "service-list",fallback = ListFeignClientFallback.class)
public interface ListFeignClient {

    //暴露给访问详情微服务使用
    @PostMapping("/api/list/incrHotScore/{skuId}/{score}")
    Result incrHotScore(@PathVariable Long skuId, @PathVariable Integer score);

    @PostMapping("/api/list/searchResponseVo")
    SearchResponseVo searchResponseVo(@RequestBody SearchParam searchParam);
}
