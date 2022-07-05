package com.pdmxz.gmall.cart.feign.impl;

import com.pdmxz.gmall.cart.feign.CartFeignClient;
import com.pdmxz.gmall.model.cart.CartInfo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartFeignClientFallback implements CartFeignClient {
    @Override
    public void addToCart(Long skuId, Integer skuNum) {

    }

    @Override
    public CartInfo findCartInfo(Long skuId) {
        return null;
    }

    @Override
    public List<CartInfo> getCartInfoToTrade() {
        return null;
    }
}
