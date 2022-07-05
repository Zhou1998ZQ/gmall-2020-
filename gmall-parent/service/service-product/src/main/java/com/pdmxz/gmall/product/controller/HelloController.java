package com.pdmxz.gmall.product.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HelloController {

    @GetMapping("/hello")
    public String hello(Model model, HttpSession session){
        model.addAttribute("request","HELLLLLO");
        session.setAttribute("session","world");
        List list = new ArrayList();
        list.add(1);
        list.add(1);
        list.add(1);
        model.addAttribute("list",list);
        return "hello";
    }
}
