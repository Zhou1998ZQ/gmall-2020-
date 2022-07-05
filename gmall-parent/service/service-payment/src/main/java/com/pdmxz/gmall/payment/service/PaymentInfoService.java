package com.pdmxz.gmall.payment.service;

import com.pdmxz.gmall.model.enums.PaymentType;
import com.pdmxz.gmall.model.payment.PaymentInfo;

import java.util.Map;

public interface PaymentInfoService {
    PaymentInfo savePaymentInfoByOrderId(Long orderId, PaymentType paymentType);

    PaymentInfo getPaymentInfoByOutTradeNo(String out_trade_no);

    void updatePaymentInfo(Map<String, String> paramMap, PaymentInfo paymentInfo);
}
