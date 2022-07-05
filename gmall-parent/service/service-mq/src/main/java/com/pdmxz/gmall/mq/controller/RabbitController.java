package com.pdmxz.gmall.mq.controller;

import com.pdmxz.gmall.common.result.Result;
import com.pdmxz.gmall.common.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mq")
public class RabbitController {

    @Autowired
    private RabbitService rabbitService;

    @GetMapping("/try/{msg}")
    public Result sendMessage(@PathVariable String msg){
        rabbitService.sendMessage("e1","k1",msg);
        System.out.println(msg);
        return Result.ok();
    }

    @GetMapping("/tryTTL/{msg}")
    public Result sendTTLMessage(@PathVariable String msg){
        rabbitService.sendTTLMessage("TTLExchange","Key",msg,10);
        return Result.ok();
    }

    @GetMapping("/tryPluginTTL/{msg}")
    public Result tryPluginTTL(@PathVariable String msg){
        rabbitService.sendPluginTTLMessage("pluginExchange","pluginKey",msg,10);
        return Result.ok();
    }


}


