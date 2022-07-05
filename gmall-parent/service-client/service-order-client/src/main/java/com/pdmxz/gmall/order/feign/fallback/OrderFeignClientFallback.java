package com.pdmxz.gmall.order.feign.fallback;

import com.pdmxz.gmall.model.order.OrderInfo;
import com.pdmxz.gmall.order.feign.OrderFeignClient;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrderFeignClientFallback implements OrderFeignClient {
    @Override
    public Map getTrade() {
        return null;
    }

    @Override
    public OrderInfo getOrderInfoById(Long orderId) {
        return null;
    }
}
