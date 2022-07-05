package com.pdmxz.gmall.activity.service;

import com.pdmxz.gmall.model.activity.SeckillGoods;

import java.util.List;

public interface ActivityInfoService {

    void sendActivityInfoToRedis();

    List<SeckillGoods> getAllSeckillGoods();

    SeckillGoods getSeckillGoodsBySkuId(Long skuId);
}
