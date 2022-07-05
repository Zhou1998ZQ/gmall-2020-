package com.pdmxz.gmall.all.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PassportController {

    //转发到登录页面
    @GetMapping("/login.html")
    public String login(String originUrl, Model model){
        model.addAttribute("originUrl",originUrl);
        return "login";
    }
}
