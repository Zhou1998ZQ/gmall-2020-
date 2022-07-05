package com.pdmxz.gmall.cart.controller;


import com.pdmxz.gmall.cart.service.CartInfoService;
import com.pdmxz.gmall.common.result.Result;
import com.pdmxz.gmall.common.util.AuthContextHolder;
import com.pdmxz.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartInfoController {

    @Autowired
    private CartInfoService cartInfoService;

    @PostMapping("/addToCart/{skuId}/{skuNum}")
    public void addToCart(@PathVariable Long skuId, @PathVariable Integer skuNum,HttpServletRequest httpServletRequest){
        String userId = AuthContextHolder.getUserId(httpServletRequest);
        String userTempId = AuthContextHolder.getUserTempId(httpServletRequest);
        System.out.println(userId);
        System.out.println(userTempId);
        cartInfoService.addToCart(skuId,skuNum);
    }

    @GetMapping("/toCart/{skuId}")
    public CartInfo findCartInfo(@PathVariable Long skuId){
        return cartInfoService.findCartInfo(skuId);
    }

    //'http://api.gmall.com/api/cart/cartList'get
    @GetMapping("/cartList")
    public Result getCartList(HttpServletRequest httpServletRequest){
        String userId = AuthContextHolder.getUserId(httpServletRequest);
        String userTempId = AuthContextHolder.getUserTempId(httpServletRequest);
        List<CartInfo> cartInfoList = cartInfoService.getCartList(userId,userTempId);
        return Result.ok(cartInfoList);
    }

    ///checkCart/' + skuId + '/' + isChecked
    //复选框
    @GetMapping("/checkCart/{skuId}/{isChecked}")
    public Result checkCart(@PathVariable Long skuId,@PathVariable Integer isChecked){
        cartInfoService.checkCart(skuId,isChecked);
        return Result.ok();
    }

    //去缓存中查询isCheck为1的商品集合
    @GetMapping("/getCartInfoToTrade")
    public List<CartInfo> getCartInfoToTrade(HttpServletRequest httpServletRequest){
        String userId = AuthContextHolder.getUserId(httpServletRequest);
       return cartInfoService.getCartInfoToTrade(userId);
    }

}
