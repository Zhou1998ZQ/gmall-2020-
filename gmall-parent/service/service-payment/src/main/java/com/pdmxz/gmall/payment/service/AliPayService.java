package com.pdmxz.gmall.payment.service;

public interface AliPayService {
    String createAliPay(Long orderId);

    String refund(String outTradeNo);
}
