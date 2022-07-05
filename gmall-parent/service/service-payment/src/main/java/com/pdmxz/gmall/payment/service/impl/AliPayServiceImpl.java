package com.pdmxz.gmall.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.pdmxz.gmall.model.enums.PaymentType;
import com.pdmxz.gmall.model.payment.PaymentInfo;
import com.pdmxz.gmall.payment.config.AlipayConfig;
import com.pdmxz.gmall.payment.service.AliPayService;
import com.pdmxz.gmall.payment.service.PaymentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;

@Service
public class AliPayServiceImpl implements AliPayService {

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private PaymentInfoService paymentInfoService;

    @Override
    public String refund(String outTradeNo) {
        PaymentInfo paymentInfo = paymentInfoService.getPaymentInfoByOutTradeNo(outTradeNo);
        //退款
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();

        BigDecimal totalAmount = paymentInfo.getTotalAmount();
        model.setOutTradeNo(outTradeNo);
        model.setRefundAmount(totalAmount.toString());
        request.setBizModel(model);
        try {
            AlipayTradeRefundResponse response = alipayClient.execute(request);
            response.getBody();
            if (response.isSuccess()){
                return "success";
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String createAliPay(Long orderId) {
        try {
        PaymentInfo paymentInfo = paymentInfoService.savePaymentInfoByOrderId(orderId, PaymentType.ALIPAY);
        // 生产二维码
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();//创建API对应的request
        // 同步回调
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        // 异步回调
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);//在公共参数中设置回跳和通知地址

        HashMap<String, Object> map = new HashMap<>();
        map.put("out_trade_no",paymentInfo.getOutTradeNo());
        map.put("product_code","FAST_INSTANT_TRADE_PAY");
        map.put("total_amount",paymentInfo.getTotalAmount());
        map.put("subject",paymentInfo.getSubject());

        alipayRequest.setBizContent(JSON.toJSONString(map));
            return alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单;
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return "";
        }
    }
}
