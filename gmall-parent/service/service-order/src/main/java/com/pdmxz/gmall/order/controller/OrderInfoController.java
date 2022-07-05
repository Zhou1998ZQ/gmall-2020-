package com.pdmxz.gmall.order.controller;

import com.pdmxz.gmall.cart.feign.CartFeignClient;
import com.pdmxz.gmall.common.result.Result;
import com.pdmxz.gmall.common.util.AuthContextHolder;
import com.pdmxz.gmall.model.cart.CartInfo;
import com.pdmxz.gmall.model.order.OrderDetail;
import com.pdmxz.gmall.model.order.OrderInfo;
import com.pdmxz.gmall.model.user.UserAddress;
import com.pdmxz.gmall.order.service.OrderInfoService;
import com.pdmxz.gmall.user.feign.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/order")
public class OrderInfoController {

    @Autowired
    private CartFeignClient cartFeignClient;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderInfoService orderInfoService;

    /**
     * 回显trade页面
     *
     * @return
     */
    @GetMapping("/auth/trade")
    public Map getTrade(HttpServletRequest request) {

        Map map = new HashMap();
        List<UserAddress> userAddress = userFeignClient.getUserAddress();
        //收货地址
        map.put("userAddressList", userAddress);

        List<CartInfo> cartInfoToTrade = cartFeignClient.getCartInfoToTrade();
        List<OrderDetail> orderDetailList = new ArrayList<>();
        //将cartInfo进行遍历-->转化成OrderDetail
        for (CartInfo cartInfo : cartInfoToTrade) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setOrderPrice(cartInfo.getSkuPrice());
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            orderDetailList.add(orderDetail);
        }
        //商品栏
        map.put("detailArrayList", orderDetailList);
        long totalNum = cartInfoToTrade.stream().collect(Collectors.summarizingInt(CartInfo::getSkuNum)).getSum();
        //商品总数
        map.put("totalNum", totalNum);
        BigDecimal bigDecimal = new BigDecimal(0);
        for (OrderDetail orderDetail : orderDetailList) {
            bigDecimal = bigDecimal.add(orderDetail.getOrderPrice().multiply(new BigDecimal(orderDetail.getSkuNum())));
        }
        //总价格
        map.put("totalAmount", bigDecimal);

        //生成uuid作为交易号  避免表单重复提交 并存入redis中(倒时进行校验)
        String uuid = UUID.randomUUID().toString();
        String userId = AuthContextHolder.getUserId(request);
        redisTemplate.opsForValue().set("tradeNo:" + userId, uuid);
        map.put("tradeNo", uuid);
        return map;
    }


    ///auth/submitOrder?tradeNo=' + tradeNo 提交订单ajax
    @PostMapping("/auth/submitOrder")
    public Result submitOrder(@RequestBody OrderInfo orderInfo, String tradeNo, HttpServletRequest request) {
        //校验交易号
        String userId = AuthContextHolder.getUserId(request);
        String o = (String) redisTemplate.opsForValue().get("tradeNo:" + userId);
        if (o != null && tradeNo.equals(o)) {
            //验证成功
            redisTemplate.delete("tradeNo:" + userId);
            //连接仓库系统
            List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
            for (OrderDetail orderDetail : orderDetailList) {
                Long skuId = orderDetail.getSkuId();
                Integer skuNum = orderDetail.getSkuNum();
                boolean flag = orderInfoService.checkStock(skuId, skuNum);
                if (!flag) {
                    return Result.fail().message(orderDetail.getSkuName() + "改商品没有库存");
                }
            }
            //代码走到这里说明有库存
            //保存订单
            orderInfo.setUserId(Long.parseLong(userId));
            Long orderId = orderInfoService.saveOrder(orderInfo);

            return Result.ok(orderId);
        } else {
            return Result.fail().message("避免重复提交订单");
        }

    }


    @GetMapping("/getOrderInfoById/{orderId}")
    public OrderInfo getOrderInfoById (@PathVariable Long orderId) {
        return orderInfoService.getOrderInfoById(orderId);
    }


}
