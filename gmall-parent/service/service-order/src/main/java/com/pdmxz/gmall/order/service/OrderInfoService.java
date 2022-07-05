package com.pdmxz.gmall.order.service;

import com.pdmxz.gmall.model.order.OrderInfo;

public interface OrderInfoService {

    boolean checkStock(Long skuId, Integer skuNum);

    Long saveOrder(OrderInfo orderInfo);

    OrderInfo getOrderInfoById(Long orderId);
}
