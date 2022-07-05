package com.pdmxz.gmall.list.config;

import com.pdmxz.gmall.common.constant.MqConst;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {

    @Bean
    public Exchange exchange(){
        return ExchangeBuilder.directExchange(MqConst.EXCHANGE_DIRECT_GOODS).durable(true).build();
    }

    @Bean
    public Queue queue(){
        return QueueBuilder.durable(MqConst.QUEUE_GOODS_UPPER).build();
    }

    @Bean
    public Binding binding (@Qualifier("queue") Queue queue,
                            @Qualifier("exchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(MqConst.ROUTING_GOODS_UPPER).noargs();
    }

    @Bean
    public Queue queue1(){
        return QueueBuilder.durable(MqConst.QUEUE_GOODS_LOWER).build();
    }

    @Bean
    public Binding binding1 (@Qualifier("queue1") Queue queue1,
                            @Qualifier("exchange") Exchange exchange){
        return BindingBuilder.bind(queue1).to(exchange).with(MqConst.ROUTING_GOODS_LOWER).noargs();
    }

}
