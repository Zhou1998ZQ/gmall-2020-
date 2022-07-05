package com.pdmxz.gmall.item.feign;

import com.pdmxz.gmall.common.result.Result;
import com.pdmxz.gmall.item.feign.fallback.ItemFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-item",fallback = ItemFeignClientFallback.class)
public interface ItemFeignClient {

    @GetMapping("/api/item/{skuId}")
    public Result getItem(@PathVariable Long skuId);

}
