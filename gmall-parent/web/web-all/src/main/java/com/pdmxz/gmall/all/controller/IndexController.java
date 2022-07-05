package com.pdmxz.gmall.all.controller;

import com.pdmxz.gmall.common.result.Result;
import com.pdmxz.gmall.feign.ProductFeignClient;
import com.pdmxz.gmall.model.product.BaseCategoryView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class IndexController {

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private TemplateEngine templateEngine;

    @GetMapping("/")
    public String index(Model model) {
        List<Map> data = getData();
        model.addAttribute("list", data);
        return "index/index";
    }

    /**
     * 首页三级分类
     * @return
     */
    public List<Map> getData(){
        List<BaseCategoryView> baseCategoryViewList = productFeignClient.getIndex();
        List<Map> mapList = new ArrayList<>();
        int index = 0 ;
        Map<Long, List<BaseCategoryView>> collect = baseCategoryViewList.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        //一级分类
        for (Map.Entry<Long, List<BaseCategoryView>> categoryMap1 : collect.entrySet()) {
            Map map1 = new HashMap<>();
            Long categoryMap1Key = categoryMap1.getKey();
            List<BaseCategoryView> value = categoryMap1.getValue();
            String category1Name = value.get(0).getCategory1Name();
            map1.put("categoryId",categoryMap1Key);
            map1.put("categoryName",category1Name);
            map1.put("index",index);
            index++;
            //二级分类
            List<Map> mapList2 = new ArrayList<>();//用于存储二级分类
            for (Map.Entry<Long, List<BaseCategoryView>> categoryMap2 : value.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id)).entrySet()) {
                Long categoryMap2Key = categoryMap2.getKey();
                Map map2 = new HashMap<>();
                List<BaseCategoryView> categoryMap2Value = categoryMap2.getValue();
                String category2Name = categoryMap2Value.get(0).getCategory2Name();
                map2.put("categoryId",categoryMap2Key);
                map2.put("categoryName",category2Name);
                List<Map> mapList3 = new ArrayList<>();//用于存储三级分类
                for (BaseCategoryView category3 : categoryMap2Value) {
                    Long category3Id = category3.getCategory3Id();
                    String categoryName = category3.getCategory3Name();
                    Map map3 = new HashMap();
                    map3.put("categoryName",categoryName);
                    map3.put("categoryId",category3Id);
                    mapList3.add(map3);
                }
                map2.put("categoryChild",mapList3);
                mapList2.add(map2);
            }
            map1.put("categoryChild",mapList2);
            mapList.add(map1);
        }
        return mapList;
    }

    /**
     * 使用静态化技术
     * @return
     */
    @GetMapping("/createHtml")
    @ResponseBody
    public Result createHtml(){
        List<Map> list = getData();
        Context context = new Context();
        context.setVariable("list",list);
        Writer writer = null;
        try {
            writer = new PrintWriter("D:\\Java\\atguigu-mall\\StaticIndexHtml\\index.html","UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        templateEngine.process("index/index.html",context,writer);
        return Result.ok();
    }
}
