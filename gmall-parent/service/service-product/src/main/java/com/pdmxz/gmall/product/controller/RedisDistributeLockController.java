package com.pdmxz.gmall.product.controller;


import com.pdmxz.gmall.common.result.Result;
import com.pdmxz.gmall.product.service.RedisDistributeLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/product/test")
public class RedisDistributeLockController {

    @Autowired
    private RedisDistributeLockService redisDistributeLockService;
    @RequestMapping("/testLock")
    public Result testLock(){
        redisDistributeLockService.testLock();
        return Result.ok();
    }
}
