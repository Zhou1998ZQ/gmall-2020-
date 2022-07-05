package com.pdmxz.gmall.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PluginTTLConfig {

    @Bean
    public Exchange pluginExchange(){
        Map<String,Object> map = new HashMap<>();
        map.put("x-delayed-type","direct");
        return new CustomExchange("pluginExchange","x-delayed-message",true,false,map);
    }

    @Bean
    public Queue pluginQueue(){
        return QueueBuilder.durable("pluginQueue").build();
    }

    @Bean
    public Binding pluginBinding(@Qualifier ("pluginQueue") Queue pluginQueue,@Qualifier ("pluginExchange")Exchange pluginExchange){
        return BindingBuilder.bind(pluginQueue).to(pluginExchange).with("pluginKey").noargs();
    }
}
