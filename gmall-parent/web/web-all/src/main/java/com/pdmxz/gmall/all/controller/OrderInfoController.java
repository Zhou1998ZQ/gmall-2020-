package com.pdmxz.gmall.all.controller;

import com.pdmxz.gmall.order.feign.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class OrderInfoController {

    @Autowired
    private OrderFeignClient orderFeignClient;

    @GetMapping("/trade.html")
    public String trade(Model model){
        Map map = orderFeignClient.getTrade();
        model.addAllAttributes(map);
        return "order/trade";
    }
}
