package com.pdmxz.gmall.cart.service;

import com.pdmxz.gmall.model.cart.CartInfo;

import java.util.List;

public interface CartInfoService {
    void addToCart(Long skuId, Integer skuNum);

    CartInfo findCartInfo(Long skuId);

    List<CartInfo> getCartList(String userId, String userTempId);

    void checkCart(Long skuId, Integer isChecked);

    List<CartInfo> getCartInfoToTrade(String userId);
}
