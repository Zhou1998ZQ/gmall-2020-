package com.pdmxz.gmall.all.controller;

import com.pdmxz.gmall.activity.feign.ActivityFeignClient;
import com.pdmxz.gmall.model.activity.SeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class SeckillController {

    @Autowired
    private ActivityFeignClient activityFeignClient;


    @GetMapping("/seckill/index")
    public String indexSeckill(Model model){
        List<SeckillGoods> allSeckillGoods = activityFeignClient.getAllSeckillGoods();
        model.addAttribute("list",allSeckillGoods);
        return "seckill/index";
    }

    @GetMapping("/seckill/{skuId}.html")
    public String showItem(@PathVariable Long skuId,Model model){
        SeckillGoods seckillGoodsBySkuId = activityFeignClient.getSeckillGoodsBySkuId(skuId);
        model.addAttribute("item",seckillGoodsBySkuId);
        return "seckill/item";
    }


}
