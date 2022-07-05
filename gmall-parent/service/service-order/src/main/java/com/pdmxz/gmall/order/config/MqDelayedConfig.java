package com.pdmxz.gmall.order.config;

import com.pdmxz.gmall.common.constant.MqConst;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MqDelayedConfig {
    //交换机 基于插件
    @Bean
    public Exchange orderExchange() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-delayed-type", "direct");
        return new CustomExchange(MqConst.EXCHANGE_DIRECT_ORDER_CANCEL, "x-delayed-message", true, false, arguments);
    }

    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable(MqConst.QUEUE_ORDER_CANCEL).build();
    }

    @Bean
    public Binding orderBinding(@Qualifier("orderQueue") Queue orderQueue, @Qualifier("orderExchange") Exchange orderExchange) {
        return BindingBuilder.bind(orderQueue).to(orderExchange).with(MqConst.ROUTING_ORDER_CANCEL).noargs();
    }
}
