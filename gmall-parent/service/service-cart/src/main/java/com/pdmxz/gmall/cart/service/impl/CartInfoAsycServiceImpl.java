package com.pdmxz.gmall.cart.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pdmxz.gmall.cart.mapper.CartInfoMapper;
import com.pdmxz.gmall.cart.service.CartInfoAsycService;
import com.pdmxz.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class CartInfoAsycServiceImpl implements CartInfoAsycService {

    @Autowired
    private CartInfoMapper cartInfoMapper;

    @Async
    @Override
    public void insert(CartInfo cartInfo1) {
        cartInfoMapper.insert(cartInfo1);
    }

    @Async
    @Override
    public void update(CartInfo cartInfo) {
        cartInfoMapper.update(cartInfo,new QueryWrapper<CartInfo>()
                        .eq("user_id",cartInfo.getUserId())
        .eq("sku_id",cartInfo.getSkuId())
        );
    }
}
