package com.pdmxz.gmall.all.controller;

import com.pdmxz.gmall.model.order.OrderInfo;
import com.pdmxz.gmall.order.feign.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentController {

    //top指令 3个数/3 大于60
    @Autowired
    private OrderFeignClient orderFeignClient;



    @GetMapping("/pay.html")
    public String paymentPage(Long orderId, Model model){
        OrderInfo orderInfo = orderFeignClient.getOrderInfoById(orderId);
        model.addAttribute("orderInfo",orderInfo);


        return "payment/pay";
    }

    @GetMapping("/pay/success.html")
    public String success(){
        return "payment/success";
    }
}
