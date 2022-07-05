package com.pdmxz.gmall.item.feign.fallback;

import com.pdmxz.gmall.common.result.Result;
import com.pdmxz.gmall.item.feign.ItemFeignClient;
import org.springframework.stereotype.Component;

@Component
public class ItemFeignClientFallback implements ItemFeignClient {
    @Override
    public Result getItem(Long skuId) {
        return null;
    }
}
