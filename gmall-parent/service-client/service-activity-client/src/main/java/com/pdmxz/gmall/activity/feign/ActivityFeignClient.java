package com.pdmxz.gmall.activity.feign;

import com.pdmxz.gmall.model.activity.SeckillGoods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "service-activity")
public interface ActivityFeignClient {
    @GetMapping("/api/activity/seckill/getAllSeckillGoods")
    List<SeckillGoods> getAllSeckillGoods();

    @GetMapping("/api/activity/seckill/getSeckillGoods/{skuId}")
    SeckillGoods getSeckillGoodsBySkuId(@PathVariable Long skuId);
}
