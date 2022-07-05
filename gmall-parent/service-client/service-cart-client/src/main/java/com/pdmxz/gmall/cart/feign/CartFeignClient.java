package com.pdmxz.gmall.cart.feign;

import com.pdmxz.gmall.cart.feign.impl.CartFeignClientFallback;
import com.pdmxz.gmall.model.cart.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


import javax.servlet.http.HttpServletRequest;
import java.util.List;

@FeignClient(name = "service-cart",fallback = CartFeignClientFallback.class)
public interface CartFeignClient {

    @PostMapping("/api/cart/addToCart/{skuId}/{skuNum}")
    void addToCart(@PathVariable Long skuId, @PathVariable Integer skuNum);

    @GetMapping("/api/cart/toCart/{skuId}")
    CartInfo findCartInfo(@PathVariable Long skuId);

    //去缓存中查询isCheck为1的商品集合
    @GetMapping("/api/cart/getCartInfoToTrade")
    List<CartInfo> getCartInfoToTrade();

}
