package com.pdmxz.gmall.payment.controller;


import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.pdmxz.gmall.model.enums.PaymentStatus;
import com.pdmxz.gmall.model.payment.PaymentInfo;
import com.pdmxz.gmall.payment.config.AlipayConfig;
import com.pdmxz.gmall.payment.service.AliPayService;
import com.pdmxz.gmall.payment.service.PaymentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/api/payment/alipay")
public class AliPayController {

    @Autowired
    private AliPayService aliPayService;

    @Autowired
    private PaymentInfoService paymentInfoService;
    @ResponseBody
    @GetMapping("/submit/{orderId}")
    public String submitOrder(@PathVariable Long orderId){
       String page = aliPayService.createAliPay(orderId);
       return page;
    }

    //同步  get  给用户看的/callback/return
    @GetMapping("/callback/return")
    public String callbackReturn(){
        return "redirect:"+ AlipayConfig.return_order_url;
    }


    //异步  post /callback/notify

    @ResponseBody
    @PostMapping("/callback/notify")
    public String callbackNotify(@RequestParam Map<String, String> paramMap){

        try {
            boolean flag = AlipaySignature.rsaCheckV1(paramMap, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);
            // 交易状态
            String trade_status = paramMap.get("trade_status");
            String out_trade_no = paramMap.get("out_trade_no");
            if (flag){
                if ("TRADE_SUCCESS".equals(trade_status)){
                   PaymentInfo paymentInfo = paymentInfoService.getPaymentInfoByOutTradeNo(out_trade_no);
                   if (paymentInfo != null && PaymentStatus.UNPAID.name().equals(paymentInfo.getPaymentStatus())){
                       paymentInfoService.updatePaymentInfo(paramMap,paymentInfo);

                       return "success";
                   }
                }
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return "failure";
    }

    //退款http://payment.gmall.com/api/payment/alipay/refund/ATGUIGU158348294514133
    @GetMapping("/refund/{outTradeNo}")
    @ResponseBody
    public String refund(@PathVariable String outTradeNo){
        return aliPayService.refund(outTradeNo);
    }

}
