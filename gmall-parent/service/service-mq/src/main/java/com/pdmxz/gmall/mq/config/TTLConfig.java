package com.pdmxz.gmall.mq.config;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TTLConfig {

    //死信交换机
    @Bean
    private Exchange deadExchange(){
        return ExchangeBuilder.directExchange("TTLExchange").build();
    }
    //死信队列
    @Bean
    private Queue queue(){
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange","TTLExchange");
        arguments.put("x-dead-letter-routing-key","TTLKey");
        return QueueBuilder.durable("deadQueue").withArguments(arguments).build();
    }

    //交换机绑定死信队列
    @Bean
    private Binding binding(@Qualifier("queue") Queue queue, @Qualifier("deadExchange") Exchange deadExchange){
        return BindingBuilder.bind(queue).to(deadExchange).with("Key").noargs();
    }

    //普通队列
    @Bean
    private Queue queue1(){
        return QueueBuilder.durable("queue").build();
    }

    //死信交换机绑定队列
    @Bean
    private Binding binding1(@Qualifier("queue1") Queue queue1, @Qualifier("deadExchange") Exchange deadExchange){
        return BindingBuilder.bind(queue1).to(deadExchange).with("TTLKey").noargs();
    }
}
