package com.pdmxz.gmall.order.feign;

import com.pdmxz.gmall.model.order.OrderInfo;
import com.pdmxz.gmall.order.feign.fallback.OrderFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "service-order",fallback = OrderFeignClientFallback.class)
public interface OrderFeignClient {

    @GetMapping("/api/order/auth/trade")
    Map getTrade();


    @GetMapping("/api/order/getOrderInfoById/{orderId}")
    OrderInfo getOrderInfoById (@PathVariable Long orderId);
}
