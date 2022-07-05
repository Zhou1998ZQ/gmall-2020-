package com.pdmxz.gmall.all.controller;

import com.pdmxz.gmall.list.feign.ListFeignClient;
import com.pdmxz.gmall.model.list.SearchParam;
import com.pdmxz.gmall.model.list.SearchResponseVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ListController {

    @Autowired
    private ListFeignClient listFeignClient;

    @GetMapping("/list.html")
    public String list(SearchParam searchParam, Model model){

        SearchResponseVo searchResponseVo = listFeignClient.searchResponseVo(searchParam);
        //入参
        model.addAttribute("searchParam",searchParam);
        model.addAttribute("propsParamList",searchParam.getProps());
        //品牌集合
        model.addAttribute("trademarkList",searchResponseVo.getTrademarkList());
        //平台属性集合
        model.addAttribute("attrsList",searchResponseVo.getAttrsList());

        //商品集合
        model.addAttribute("goodsList",searchResponseVo.getGoodsList());
        model.addAttribute("pageNo",searchResponseVo.getPageNo());
        //分页信息
        model.addAttribute("totalPages",searchResponseVo.getTotalPages());

        //品牌回显  trademark=2:华为
        String trademark = searchParam.getTrademark();
        if (!StringUtils.isEmpty(trademark)){
            String[] split = trademark.split(":");
            model.addAttribute("trademarkParam",split[1]);
        }

        //商品属性回显    props=23:4G:运行内存
        String[] props = searchParam.getProps();
        if (props != null && props.length>0){
            List<Map> propsParamList = Arrays.stream(props).map((prop)->{
                Map map = new HashMap();
                String[] strings = prop.split(":");
                map.put("attrId",strings[0]);
                map.put("attrValue",strings[1]);
                map.put("attrName",strings[2]);
                return map;
            }).collect(Collectors.toList());
            model.addAttribute("propsParamList",propsParamList);
        }


        //排序  // order=1:asc  排序规则   0:asc
        String order = searchParam.getOrder();
        if (!StringUtils.isEmpty(order)){
            String[] strings = order.split(":");
            Map orderMap = new HashMap();
            orderMap.put("type",strings[0]);
            orderMap.put("sort",strings[1]);
            model.addAttribute("orderMap",orderMap);
        }else {
            Map orderMap = new HashMap();
            orderMap.put("type","1");
            orderMap.put("sort","desc");
            model.addAttribute("orderMap",orderMap);
        }


        //url
        String urlParam = buildUrl(searchParam);
        model.addAttribute("urlParam",urlParam);
        return "list/index";
    }

    private String buildUrl(SearchParam searchParam) {
        StringBuilder stringBuilder = new StringBuilder();
        //拼接keyword
        String keyword = searchParam.getKeyword();
        stringBuilder.append("http://list.gmall.com/list.html?keyword=").append(keyword);
        //拼接trademark
        String trademark = searchParam.getTrademark();
        if (!StringUtils.isEmpty(trademark)){
            stringBuilder.append("&trademark=").append(trademark);
        }
        //拼接属性
        String[] props = searchParam.getProps();
        if (props!=null&&props.length>0){
            for (String prop : props) {
                stringBuilder.append("&prop=").append(prop);
            }
        }


        String s = stringBuilder.toString();
        return s;
    }
}
