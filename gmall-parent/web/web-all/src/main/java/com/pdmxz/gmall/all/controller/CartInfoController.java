package com.pdmxz.gmall.all.controller;


import com.pdmxz.gmall.cart.feign.CartFeignClient;
import com.pdmxz.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CartInfoController {


    @Autowired
    private CartFeignClient cartFeignClient;


    //http://cart.gmall.com/addCart.html?skuId=2&skuNum=1
    @GetMapping("/addCart.html")
    public String addCart(Long skuId,Integer skuNum, RedirectAttributes redirectAttributes){
        //写操作
        cartFeignClient.addToCart(skuId,skuNum);
        redirectAttributes.addAttribute("skuId",skuId);
        redirectAttributes.addAttribute("skuNum",skuNum);
        //重定向到
        return "redirect:http://cart.gmall.com/toCart.html";
    }

    @GetMapping("/toCart.html")
    public String toCart(Long skuId,Integer skuNum, Model model){
        CartInfo cartInfo = cartFeignClient.findCartInfo(skuId);
        cartInfo.setSkuNum(skuNum);
        model.addAttribute("cartInfo",cartInfo);
        return "cart/addCart";
    }

    ///cart.html
    @GetMapping("/cart.html")
    public String cart(){
        return "cart/index";
    }



}
