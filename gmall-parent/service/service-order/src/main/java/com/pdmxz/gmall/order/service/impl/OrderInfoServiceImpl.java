package com.pdmxz.gmall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pdmxz.gmall.common.constant.MqConst;
import com.pdmxz.gmall.common.constant.RedisConst;
import com.pdmxz.gmall.common.service.RabbitService;
import com.pdmxz.gmall.common.util.HttpClientUtil;
import com.pdmxz.gmall.feign.ProductFeignClient;
import com.pdmxz.gmall.model.cart.CartInfo;
import com.pdmxz.gmall.model.enums.OrderStatus;
import com.pdmxz.gmall.model.enums.ProcessStatus;
import com.pdmxz.gmall.model.order.OrderDetail;
import com.pdmxz.gmall.model.order.OrderInfo;
import com.pdmxz.gmall.order.mapper.CartInfoMapper;
import com.pdmxz.gmall.order.mapper.OrderDetailMapper;
import com.pdmxz.gmall.order.mapper.OrderInfoMapper;
import com.pdmxz.gmall.order.service.OrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

@Service
public class OrderInfoServiceImpl implements OrderInfoService {

    @Value("${ware.url}")
    private String wareUrl;
    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private CartInfoMapper cartInfoMapper;

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public boolean checkStock(Long skuId, Integer skuNum) {
        String s = HttpClientUtil.doGet(wareUrl + "/hasStock?skuId=" + skuId + "&num=" + skuNum);
        if ("1".equals(s)){
            return true;
        }
        return false;
    }


    @Autowired
    private RabbitService rabbitService;
    /**
     * 需要操作两张表 OrderInfo OrderDetail
     * @param orderInfo
     * @return
     */
    @Override
    public Long saveOrder(OrderInfo orderInfo) {
        //一：操作OrderInfo表
        //orderInfo需要设置
        //订单状态（对用户）
        orderInfo.setOrderStatus(OrderStatus.UNPAID.name());

        //out_trade_no  订单交易编号 （alipay)
        String outTradeNo = "Zhou"+System.currentTimeMillis()+new Random().nextInt(1000);
        orderInfo.setOutTradeNo(outTradeNo);
        //trade_body  订单交易描述 （alipay)
        StringBuilder stringBuilder = new StringBuilder();
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        BigDecimal totalAmount = new BigDecimal(0);
        for (OrderDetail orderDetail : orderDetailList) {
            stringBuilder.append(orderDetail.getSkuName());
            //获取最新价格
            BigDecimal price = productFeignClient.getPrice(orderDetail.getSkuId());
            totalAmount = totalAmount.add(price.multiply(new BigDecimal(orderDetail.getSkuNum())));


        }
        if (stringBuilder.length() > 100){
            stringBuilder.substring(0,100);
        }
        orderInfo.setTradeBody(stringBuilder.toString());
        //总金额
        orderInfo.setTotalAmount(totalAmount);
        //创建订单时间
        Calendar calendar = Calendar.getInstance();
        orderInfo.setCreateTime(calendar.getTime());
        //订单销毁时间
        calendar.add(Calendar.HOUR,2);
        orderInfo.setExpireTime(calendar.getTime());
        //订单状态（对内部员工）
        orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());

        orderInfoMapper.insert(orderInfo);

        List<String> list = new ArrayList<>();
        //二：操作OrderDetail表
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrderId(orderInfo.getId());
            orderDetailMapper.insert(orderDetail);

           list.add(orderDetail.getSkuId().toString());
        }


        //删除购物车中已经购买了商品
        Long userId = orderInfo.getUserId();
        //DB
        cartInfoMapper.delete(new QueryWrapper<CartInfo>()
                .eq("user_id",userId)
                .in("sku_id",list)
        );
        //redis
        String cacheKey = RedisConst.USER_KEY_PREFIX+userId+RedisConst.USER_CART_KEY_SUFFIX;
        redisTemplate.opsForHash().delete(cacheKey,list.toArray());
        Long orderId = orderInfo.getId();
        //mq 将订单id放进延迟队列中
        rabbitService.sendPluginTTLMessage(MqConst.EXCHANGE_DIRECT_ORDER_CANCEL,MqConst.ROUTING_ORDER_CANCEL,orderId,20);
        return orderId;
    }

    @Override
    public OrderInfo getOrderInfoById(Long orderId) {
        return orderInfoMapper.selectById(orderId);
    }


}
