package com.pdmxz.gmall.activity.controller;

import com.pdmxz.gmall.activity.service.ActivityInfoService;
import com.pdmxz.gmall.model.activity.SeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/activity/seckill")
public class ActivityInfoController {

    @Autowired
    private ActivityInfoService activityInfoService;

    @GetMapping("/getAllSeckillGoods")
    public List<SeckillGoods> getAllSeckillGoods(){
        return activityInfoService.getAllSeckillGoods();
    }


    @GetMapping("/getSeckillGoods/{skuId}")
    public SeckillGoods getSeckillGoodsBySkuId(@PathVariable Long skuId){
       return activityInfoService.getSeckillGoodsBySkuId(skuId);
    }

}
