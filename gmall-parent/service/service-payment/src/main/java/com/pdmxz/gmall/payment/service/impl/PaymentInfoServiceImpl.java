package com.pdmxz.gmall.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pdmxz.gmall.common.constant.MqConst;
import com.pdmxz.gmall.common.service.RabbitService;
import com.pdmxz.gmall.model.enums.PaymentStatus;
import com.pdmxz.gmall.model.enums.PaymentType;
import com.pdmxz.gmall.model.order.OrderInfo;
import com.pdmxz.gmall.model.payment.PaymentInfo;
import com.pdmxz.gmall.order.feign.OrderFeignClient;
import com.pdmxz.gmall.payment.mapper.PaymentInfoMapper;
import com.pdmxz.gmall.payment.service.PaymentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Service
public class PaymentInfoServiceImpl implements PaymentInfoService {

    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

    @Autowired
    private OrderFeignClient orderFeignClient;

    @Autowired
    private RabbitService rabbitService;
    @Override
    public PaymentInfo savePaymentInfoByOrderId(Long orderId,PaymentType paymentType) {
        PaymentInfo paymentInfo = paymentInfoMapper.selectOne(new QueryWrapper<PaymentInfo>().eq("order_id", orderId));
        if (paymentInfo == null){
            OrderInfo orderInfo = orderFeignClient.getOrderInfoById(orderId);
            if (orderInfo != null){
                paymentInfo = new PaymentInfo();
                paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
                paymentInfo.setOrderId(orderId);
                paymentInfo.setPaymentType(paymentType.name());

                paymentInfo.setTotalAmount(orderInfo.getTotalAmount());
                paymentInfo.setSubject(orderInfo.getTradeBody());
                paymentInfo.setPaymentStatus(PaymentStatus.UNPAID.name());
                Calendar calendar = Calendar.getInstance();
                paymentInfo.setCreateTime(calendar.getTime());
                paymentInfoMapper.insert(paymentInfo);
            }
        }

        return paymentInfo;

    }


    @Override
    public PaymentInfo getPaymentInfoByOutTradeNo(String out_trade_no) {
        return paymentInfoMapper.selectOne(new QueryWrapper<PaymentInfo>().eq("out_trade_no",out_trade_no));

    }


    @Transactional
    @Override
    public void updatePaymentInfo(Map<String, String> paramMap, PaymentInfo paymentInfo) {
        String trade_no = paramMap.get("trade_no");
        paymentInfo.setTradeNo(trade_no);
        paymentInfo.setPaymentStatus(PaymentStatus.PAID.name());
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(paramMap.toString());
        Long orderId = paymentInfo.getOrderId();
        paymentInfoMapper.updateById(paymentInfo);

        //发送消息至订单微服务进行修改订单状态
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_PAYMENT_PAY,MqConst.ROUTING_PAYMENT_PAY,orderId);
    }
}
